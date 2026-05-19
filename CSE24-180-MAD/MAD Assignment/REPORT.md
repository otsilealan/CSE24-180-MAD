# Student Accommodation Finder — Part A Report

**Module:** CSE24-180 Mobile Application Development
**Platform:** Android (Kotlin · Jetpack Compose · Room Database)
**Package:** `com.accommodation`

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technical Stack](#2-technical-stack)
3. [A. User Management (20 marks)](#3-a-user-management-20-marks)
4. [B. Listings (25 marks)](#4-b-listings-25-marks)
5. [C. Smart Filtering & Alerts (20 marks)](#5-c-smart-filtering--alerts-20-marks)
6. [D. Deposit & Reservation (15 marks)](#6-d-deposit--reservation-15-marks)
7. [E. Extension Feature — Campus Distance & Route Navigation (20 marks)](#7-e-extension-feature--campus-distance--route-navigation-20-marks)
8. [Project Structure](#8-project-structure)
9. [Mark Summary](#9-mark-summary)

---

## 1. Project Overview

The **Student Accommodation Finder** is an Android application designed to help University of Botswana students discover, filter, and reserve accommodation in the Gaborone area. The app supports two user roles — **Student** and **Provider** — with role-based access controlling which features are available to each user type.

The database is pre-seeded with **50 student accounts**, **5 provider accounts**, and **55 property listings** across 10 Gaborone areas, covering all six accommodation types.

---

## 2. Technical Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose (Material 3) |
| Local Database | Room (SQLite) |
| State Management | ViewModel + StateFlow |
| Image Loading | Coil (`AsyncImage`) |
| Background Tasks | WorkManager (`CoroutineWorker`) |
| Maps & Navigation | Google Maps Compose SDK |
| Build System | Gradle (Kotlin DSL) |

---

## 3. A. User Management (20 marks)

### Implementation

User management is handled by `AuthViewModel`, `LoginScreen`, `RegisterScreen`, and `ProfileScreen`, backed by `UserRepository` and the `User` Room entity.

**Registration** (`RegisterScreen.kt`)
- Fields: Email, Password (with show/hide toggle), Phone Number, Student ID (Students only)
- Role selection via chip toggle: `Student` or `Provider`
- Conditional UI — Student ID field only appears when the Student role is selected
- Full input validation via `ValidationUtils` (email format, password strength, phone format)
- Passwords are stored as SHA-256 hashes — never in plain text

**Login** (`LoginScreen.kt`)
- Email and password fields with loading state indicator
- Error messages displayed inline on failed authentication
- On success, the user's `id` and `role` are passed to the navigation layer to control screen access

**Role-Based Access**
- Students can browse listings, apply filters, save preferences, and make reservations
- Providers see a "New Listing" button and can create listings; the Reserve button is hidden for Provider accounts
- Session is persisted via `SessionManager` (DataStore)

**Seed Data** (`SeedData.kt`)
- 50 students seeded with emails `student1@ub.ac.bw` … `student50@ub.ac.bw`
- 5 providers seeded with emails `provider1@mail.com` … `provider5@mail.com`
- All passwords are hashed before storage

### Key Files

| File | Responsibility |
|---|---|
| `LoginScreen.kt` | Login UI with validation and loading state |
| `RegisterScreen.kt` | Registration UI with role selection |
| `AuthViewModel.kt` | Authentication logic and state |
| `UserRepository.kt` | Database operations for users |
| `SessionManager.kt` | Persistent session storage (DataStore) |
| `ValidationUtils.kt` | Email, password, and phone validation |
| `User.kt` | Room entity: id, email, passwordHash, studentId, phone, role |

---

## 4. B. Listings (25 marks)

### Implementation

Listings are managed by `ListingsViewModel`, `ListingsScreen`, `ListingDetailScreen`, and `CreateListingScreen`, backed by `ListingRepository` and the `Listing` Room entity.

**Listing Data Model** (`Listing.kt`)

```
id, providerId, title, price (BWP), location, type,
amenities (comma-separated), availabilityDate (epoch ms),
deposit (BWP), imagePath, status ("Available" / "Reserved")
```

**Listings Display** (`ListingsScreen.kt`)
- `LazyColumn` of `ListingCard` components
- Each card shows: property image, title, price (BWP), location, distance from campus, and status badge
- Tapping a card navigates to the full detail view

**Listing Detail** (`ListingDetailScreen.kt`)
- Full-width property image (250 dp height)
- All fields displayed: rent, deposit, type, availability date, amenities
- "Reserve" button visible only to Students on Available listings
- "Route" button in the top bar opens the map screen

**Create Listing** (`CreateListingScreen.kt`) — Provider only
- Fields: Title, Rent (BWP), Deposit (BWP), Location (dropdown), Accommodation Type (dropdown)
- Amenity selection via `FilterChip` (WiFi, Water, Electricity, Parking, Furnished, Security, Laundry)
- Date picker for availability date
- Image picker using `ActivityResultContracts.GetContent` — image is compressed to max 800×800 px and saved to app internal storage via `ImageUtils.saveImage()`
- Image is required before the listing can be posted

**Images**
- Seeded listings use bundled vector drawables (`res:property_0` … `res:property_5`), each visually distinct per accommodation type
- Provider-uploaded images are stored as absolute file paths and loaded via Coil `AsyncImage`
- `ImageUtils.resolveDrawableRes()` routes between the two rendering paths

**Seed Data**
- 55 listings across 5 providers, 10 Gaborone areas, and 6 accommodation types
- Prices are realistic BWP ranges per type (e.g. Single Room: BWP 800–1 400, 2-Bedroom: BWP 2 200–3 500)
- Each listing type is assigned a unique property illustration

### Accommodation Types & Locations

| Types | Locations |
|---|---|
| Single Room, Double Room, Bachelor Flat, 1-Bedroom, 2-Bedroom, Shared House | Gaborone West, Gaborone North, Broadhurst, Tlokweng, Mogoditshane, Phakalane, Block 8, Block 9, Extension 2, Bontleng |

### Key Files

| File | Responsibility |
|---|---|
| `ListingsScreen.kt` | Listings list with cards |
| `ListingDetailScreen.kt` | Full listing detail view |
| `CreateListingScreen.kt` | Provider listing creation form |
| `ListingsViewModel.kt` | Listings state, filtering, distance calculation |
| `ListingRepository.kt` | Database operations for listings |
| `ImageUtils.kt` | Image saving, compression, and drawable resolution |
| `Listing.kt` | Room entity with all required fields |
| `SeedData.kt` | 55 seeded listings with type-matched images |

---

## 5. C. Smart Filtering & Alerts (20 marks)

### Implementation

Filtering is handled by `FilterViewModel` and `FilterScreen`, with notifications delivered by `NotificationWorker` via WorkManager.

**Filter Screen** (`FilterScreen.kt`)
- Price range: Min and Max BWP text fields
- Location: dropdown with all 10 Gaborone areas plus "Any area"
- Availability date: full `DatePicker` component
- **Apply Filters** — immediately updates the listings list
- **Save to My Preferences** — persists the current filter to the `UserPreferences` Room table for use by the notification system
- **Clear All** — resets all filters

**Filtering Logic** (`ListingsViewModel.kt` + `ListingDao`)
- Filters are applied reactively via `StateFlow` — the listings list updates instantly when filters change
- The DAO query filters on price range, location, and availability date simultaneously

**Local Notifications** (`NotificationWorker.kt`)
- Runs as a `PeriodicWorkRequest` every 15 minutes via WorkManager
- On each run, loads all saved user preferences and all available listings
- For each user's preferences, finds listings that match all saved criteria (price, location, date)
- Fires a system notification with the listing title, location, and price
- Notification taps deep-link back into the app via `PendingIntent` to `MainActivity`
- Notification channel: `accommodation_alerts`

### Key Files

| File | Responsibility |
|---|---|
| `FilterScreen.kt` | Filter UI with save-to-preferences action |
| `FilterViewModel.kt` | Filter state and preference persistence |
| `NotificationWorker.kt` | Background preference matching and notification dispatch |
| `PreferencesRepository.kt` | CRUD for saved user preferences |
| `UserPreferences.kt` | Room entity: userId, minPrice, maxPrice, location, availabilityDate |

---

## 6. D. Deposit & Reservation (15 marks)

### Implementation

The reservation flow spans `PaymentScreen`, `ReceiptScreen`, `ReservationViewModel`, and `ReservationRepository`.

**Payment Screen** (`PaymentScreen.kt`)
- Displays a payment summary card: listing title, location, and deposit amount in BWP
- Mock card form: Card Number (16 digits), Expiry (MM/YY), CVV (3 digits)
- Pay button is disabled until all fields are valid and correctly sized
- Confirmation `AlertDialog` before processing: "Confirm & Pay" or "Cancel"
- Loading indicator shown during processing
- Error messages displayed in a styled error surface if reservation fails

**Reservation Processing** (`ReservationViewModel.kt`)
- Checks listing status before processing — if already `"Reserved"`, returns an error and blocks the payment
- On success: creates a `Reservation` record with a UUID-based reference number, updates the listing status to `"Reserved"` in the database
- Both operations are performed in a single coroutine to ensure consistency

**Receipt Screen** (`ReceiptScreen.kt`)
- Displayed immediately after successful payment
- Shows: Reference Number, Accommodation title, Location, Amount Paid (BWP), Date and time
- "Return to Listings" button navigates back to the listings screen

**Duplicate Prevention**
- The listing's `status` field is checked before any reservation is created
- Reserved listings display a red "Reserved" badge and the Reserve button is hidden in the detail view
- The DAO and repository enforce the status check at the data layer

### Key Files

| File | Responsibility |
|---|---|
| `PaymentScreen.kt` | Mock payment form with confirmation dialog |
| `ReceiptScreen.kt` | Receipt display with reference number |
| `ReservationViewModel.kt` | Reservation logic, duplicate prevention, status update |
| `ReservationRepository.kt` | Database operations for reservations |
| `Reservation.kt` | Room entity: id, listingId, studentId, referenceNumber, amount, reservationDate |

---

## 7. E. Extension Feature — Campus Distance & Route Navigation (20 marks)

### Implementation

The extension feature provides campus distance calculation on every listing card and a full route map from UB Main Campus to any listing.

**Distance Calculation** (`ListingsViewModel.kt`)
- Uses the **Haversine formula** to calculate the great-circle distance between UB Main Campus (−24.6553°, 25.9086°) and each listing's location
- Location coordinates for all 10 Gaborone areas are stored in `CampusData.locationCoords`
- Distance is displayed on every listing card as `"X.X km from campus"`

```kotlin
fun distanceFromCampusKm(listing: Listing): Double {
    val coords = CampusData.locationCoords[listing.location] ?: return -1.0
    val dLat = Math.toRadians(coords.first - CampusData.CAMPUS_LAT)
    val dLon = Math.toRadians(coords.second - CampusData.CAMPUS_LON)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(CampusData.CAMPUS_LAT)) * cos(Math.toRadians(coords.first)) *
            sin(dLon / 2).pow(2)
    return 6371.0 * 2 * atan2(sqrt(a), sqrt(1 - a))
}
```

**Route Map** (`MapScreen.kt`)
- Accessible via the "Route" button in the listing detail top bar
- Renders a `GoogleMap` composable centred on the listing's location
- Places two markers: **UB Main Campus** (starting point) and the **listing** (destination)
- Draws a `Polyline` between the two points in the app's primary colour
- An info card below the map shows the destination name and "Route from UB Main Campus"

### Key Files

| File | Responsibility |
|---|---|
| `MapScreen.kt` | Google Maps view with campus-to-listing route |
| `ListingsViewModel.kt` | `distanceFromCampusKm()` using Haversine formula |
| `CampusData.kt` | Campus coordinates and location coordinate map |

---

## 8. Project Structure

```
app/src/main/java/com/accommodation/
├── MainActivity.kt
├── AccommodationApp.kt
├── AppViewModelFactory.kt
├── data/
│   ├── AppContainer.kt
│   ├── SeedData.kt
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   ├── dao/          (UserDao, ListingDao, ReservationDao, PreferencesDao)
│   │   └── entities/     (User, Listing, Reservation, UserPreferences)
│   └── repository/       (UserRepository, ListingRepository, ReservationRepository, PreferencesRepository)
├── domain/
│   ├── FilterParams.kt
│   └── CampusData.kt
├── ui/
│   ├── auth/             (LoginScreen, RegisterScreen, ProfileScreen, AuthViewModel)
│   ├── listings/         (ListingsScreen, ListingDetailScreen, CreateListingScreen, ListingsViewModel)
│   ├── filter/           (FilterScreen, FilterViewModel)
│   ├── reservation/      (PaymentScreen, ReceiptScreen, ReservationViewModel)
│   ├── navigation/       (MapScreen, NavigationViewModel)
│   └── theme/            (Theme, Color, Type)
├── utils/
│   ├── ImageUtils.kt
│   ├── ValidationUtils.kt
│   └── SessionManager.kt
└── workers/
    └── NotificationWorker.kt

app/src/main/res/drawable/
├── property_0.xml   (Single Room)
├── property_1.xml   (Double Room)
├── property_2.xml   (Bachelor Flat)
├── property_3.xml   (1-Bedroom)
├── property_4.xml   (2-Bedroom)
└── property_5.xml   (Shared House)
```

---

## 9. Mark Summary

| Feature | Marks Available | Implementation Status |
|---|---|---|
| A. User Management | 20 | Registration, login, role-based access (Student/Provider), 50 students + 5 providers seeded, password hashing, session persistence |
| B. Listings | 25 | All required fields, image upload + display, 55 seeded records, 6 types across 10 locations, provider create form |
| C. Smart Filtering & Alerts | 20 | Price/location/date filters, save preferences, WorkManager notifications matching saved preferences every 15 minutes |
| D. Deposit & Reservation | 15 | Mock payment form, confirmation dialog, UUID reference number, receipt screen, status change to "Reserved", duplicate prevention |
| E. Extension Feature | 20 | Haversine distance on every listing card, Google Maps route from UB Main Campus to listing |
| **Total** | **100** | **All features implemented** |
