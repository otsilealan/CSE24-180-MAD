# Student Accommodation Finder — Screencast Report

**Module:** CSE24-180 Mobile Application Development
**Platform:** Android · Kotlin · Jetpack Compose
**Presenter:** [Your Name] | [Student ID]

---

## Table of Contents

1. [Introduction & Motivation](#1-introduction--motivation)
2. [Purpose of the Application](#2-purpose-of-the-application)
3. [Design, Database Schema & Screenshots](#3-design-database-schema--screenshots)
4. [Software Products & Tools](#4-software-products--tools)
5. [Screencast Scene Plan](#5-screencast-scene-plan)
6. [References](#6-references)

---

## 1. Introduction & Motivation

### Why This Application?

Finding student accommodation in Gaborone is a significant challenge. University of Botswana students arriving from outside the city must locate, evaluate, and secure housing — often before the semester begins — with no centralised platform to help them.

Key pain points this app addresses:

- **Scattered information** — listings are shared informally via WhatsApp groups and notice boards
- **No deposit transparency** — students are surprised by deposit amounts at the point of signing
- **No distance awareness** — students cannot easily judge how far a property is from campus
- **No reservation security** — double-booking and verbal agreements leave students unprotected

The **Student Accommodation Finder** solves these problems with a single mobile app that connects students with verified providers, shows real deposit amounts upfront, calculates campus distance automatically, and locks a listing the moment a deposit is paid.

### Tools & Technologies Used

| Tool | Purpose |
|---|---|
| Android Studio | IDE for development and emulator |
| Kotlin | Primary programming language |
| Jetpack Compose | Declarative UI framework (Material 3) |
| Room (SQLite) | Local relational database |
| WorkManager | Background notification scheduling |
| Google Maps Compose SDK | Campus distance and route visualisation |
| Coil | Asynchronous image loading |
| Gradle (Kotlin DSL) | Build system |

---

## 2. Purpose of the Application

### The Problem Space

The student housing market in Gaborone presents both a **social challenge** and a **business opportunity**:

- UB enrols thousands of students annually, many of whom need off-campus accommodation
- Providers (landlords) have no structured channel to reach student tenants
- There is no mechanism to prevent a landlord from accepting multiple deposits for the same room

### Business Rules & Strategies

| Rule | Implementation |
|---|---|
| A listing can only be reserved by one student | Status changes to `"Reserved"` on payment; all other students see it locked |
| Only Providers can create listings | Role-based access enforced throughout the app |
| Students must pay a deposit to confirm a reservation | Payment screen is the only path to changing listing status |
| Notifications only fire for listings that match saved preferences | WorkManager checks preferences against available listings every 15 minutes |
| Passwords are never stored in plain text | SHA-256 hashing applied before database insertion |

### Benefits

- **Students** — centralised search, transparent pricing, instant reservation confirmation with a reference number
- **Providers** — structured listing creation, automatic status management, wider reach to the student market
- **Institution** — reduced housing disputes, documented reservation trail

---

## 3. Design, Database Schema & Screenshots

### 3.1 Application Architecture

The app follows the **MVVM (Model-View-ViewModel)** pattern with a clean separation of layers:

```
UI Layer (Compose Screens)
        ↕
ViewModel Layer (StateFlow / coroutines)
        ↕
Repository Layer
        ↕
Room Database (DAOs + Entities)
```

**Navigation flow:**

```
Login / Register
      ↓
Listings Screen ──→ Filter Sheet
      ↓
Listing Detail ──→ Map / Route Screen
      ↓
Payment Screen
      ↓
Receipt Screen
```

---

### 3.2 Database Schema

**Table: `users`**

| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| email | TEXT | Unique |
| passwordHash | TEXT | SHA-256 hash |
| studentId | TEXT | Nullable (Providers have none) |
| phone | TEXT | |
| role | TEXT | `"Student"` or `"Provider"` |

**Table: `listings`**

| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| providerId | INTEGER | FK → users.id |
| title | TEXT | |
| price | REAL | BWP/month |
| location | TEXT | Gaborone area |
| type | TEXT | Single Room / Double Room / Bachelor Flat / 1-Bedroom / 2-Bedroom / Shared House |
| amenities | TEXT | Comma-separated: WiFi, Water, Electricity, Parking, Furnished, Security, Laundry |
| availabilityDate | INTEGER | Epoch milliseconds |
| deposit | REAL | BWP |
| imagePath | TEXT | `res:property_X` or absolute file path |
| status | TEXT | `"Available"` or `"Reserved"` |

**Table: `reservations`**

| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| listingId | INTEGER | FK → listings.id |
| studentId | INTEGER | FK → users.id |
| referenceNumber | TEXT | UUID-based |
| amount | REAL | Deposit paid (BWP) |
| reservationDate | INTEGER | Epoch milliseconds |

**Table: `user_preferences`**

| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | Auto-generated |
| userId | INTEGER | FK → users.id |
| minPrice | REAL | 0.0 = no minimum |
| maxPrice | REAL | 0.0 = no maximum |
| location | TEXT | Blank = any area |
| availabilityDate | INTEGER | 0 = any date |

---

### 3.3 Entity Relationship Diagram

```
┌─────────┐         ┌──────────┐         ┌──────────────────┐
│  users  │ 1 ───── N│ listings │         │ user_preferences │
│         │         │          │         │                  │
│ id (PK) │         │ id (PK)  │         │ id (PK)          │
│ email   │         │providerId│         │ userId (FK)      │
│ role    │         │ title    │         │ minPrice         │
│ ...     │         │ price    │         │ maxPrice         │
└─────────┘         │ status   │         │ location         │
     │              └──────────┘         └──────────────────┘
     │                   │
     │ 1             N   │
     └──── reservations ─┘
               │
               │ id (PK)
               │ listingId (FK)
               │ studentId (FK)
               │ referenceNumber
               │ amount
               └─────────────────
```

---

### 3.4 Key Screens (Screenshot Descriptions for Screencast)

**Screen 1 — Login**
Clean centred layout. Email and password fields with show/hide toggle. Loading spinner on the button during authentication. Error message appears inline below the password field on failure.

**Screen 2 — Register**
Role chip selector at the top (Student / Provider). Student ID field appears conditionally only when Student is selected. Scrollable form with full validation feedback.

**Screen 3 — Listings**
`LazyColumn` of property cards. Each card shows: property illustration, title, price in BWP, location, distance from UB campus, and a colour-coded status badge (green = Available, red = Reserved). Filter icon in the top bar.

**Screen 4 — Filter Sheet**
Bottom sheet with price range (min/max), location dropdown (10 Gaborone areas), and a date picker. "Save to My Preferences" persists the filter for background notifications.

**Screen 5 — Listing Detail**
Full-width property image. All fields displayed in a structured layout: rent, deposit, type, availability date, amenities. Reserve button visible only to Students on Available listings. Route button in the top bar.

**Screen 6 — Payment**
Payment summary card showing listing and deposit amount. Mock card form (number, expiry, CVV) with field-length validation. Confirmation dialog before processing. Loading state during reservation.

**Screen 7 — Receipt**
Large green checkmark. Reference number, accommodation name, location, amount paid, and timestamp displayed in a bordered card. "Return to Listings" button.

**Screen 8 — Route Map**
Google Map centred on the listing. Two markers: UB Main Campus (start) and the listing (destination). A polyline connects them. Info card below shows destination name and "Route from UB Main Campus".

---

### 3.5 Campus Distance Calculation

Distance from UB Main Campus to each listing is calculated using the **Haversine formula**, which gives the great-circle distance between two points on a sphere given their latitudes and longitudes.

**UB Main Campus coordinates:** −24.6553°, 25.9086°

The formula used:

```
a = sin²(Δlat/2) + cos(lat1) · cos(lat2) · sin²(Δlon/2)
d = 2R · atan2(√a, √(1−a))     where R = 6371 km
```

This distance is displayed on every listing card, giving students an immediate sense of commute before opening the detail view.

---

## 4. Software Products & Tools

### Platform Requirements

| Requirement | Specification |
|---|---|
| Minimum Android version | Android 8.0 (API 26) |
| Target Android version | Android 14 (API 34) |
| Development OS | Windows / macOS / Linux |
| IDE | Android Studio Hedgehog or later |
| JDK | Java 17+ |
| Build tool | Gradle 9.0 with Kotlin DSL |

### Data Display

- **Lists** — Jetpack Compose `LazyColumn` with `key`-based diffing for efficient updates
- **Images** — Bundled vector drawables for seeded listings; Coil `AsyncImage` for provider-uploaded photos (compressed to max 800×800 px, stored in app internal storage)
- **Maps** — Google Maps Compose SDK renders the interactive map; a `Polyline` overlays the campus-to-listing route
- **Notifications** — Android `NotificationCompat` via WorkManager, firing when a new listing matches a user's saved preferences

### Database

- **Room** (SQLite wrapper) provides compile-time verified SQL queries, `Flow`-based reactive queries, and automatic migration support
- All queries are executed on background coroutine dispatchers — the UI thread is never blocked
- The database is pre-seeded on first launch with 50 students, 5 providers, and 55 listings

### Links to Key Libraries

| Library | URL |
|---|---|
| Jetpack Compose | https://developer.android.com/jetpack/compose |
| Room Database | https://developer.android.com/training/data-storage/room |
| WorkManager | https://developer.android.com/topic/libraries/architecture/workmanager |
| Coil Image Loading | https://coil-kt.github.io/coil/ |
| Google Maps Compose | https://developers.google.com/maps/documentation/android-sdk/maps-compose |
| Material 3 | https://m3.material.io/ |

---

## 5. Screencast Scene Plan

Use this as your recording script. Each scene maps to a slide or live demo segment.

---

### Scene 1 — Title Slide (0:00 – 0:30)
**Slide content:** App name, your name, module code, date.
**Voice-over:** "This screencast presents the Student Accommodation Finder, a mobile application built for Android using Kotlin and Jetpack Compose. The app helps University of Botswana students find, filter, and reserve accommodation in Gaborone."

---

### Scene 2 — Introduction & Motivation (0:30 – 1:30)
**Slide content:** Bullet points — the problem (scattered listings, no deposit transparency, no distance info). A photo or icon of a student searching for housing.
**Voice-over:** Walk through each pain point. Explain why a mobile-first solution is appropriate for a student audience.

---

### Scene 3 — Purpose & Business Rules (1:30 – 2:30)
**Slide content:** Table of business rules (one-reservation-per-listing, role-based access, deposit-locks-listing).
**Voice-over:** "The app enforces strict business rules. Once a student pays a deposit, the listing is immediately marked Reserved and no other student can book it. Providers and Students have different views of the same app."

---

### Scene 4 — Architecture Diagram (2:30 – 3:00)
**Slide content:** MVVM layer diagram and navigation flow diagram (from Section 3.1).
**Voice-over:** "The app uses the MVVM pattern. Compose screens observe StateFlow from ViewModels. ViewModels call Repositories, which talk to the Room database. This keeps the UI reactive and the business logic testable."

---

### Scene 5 — Database Schema (3:00 – 3:45)
**Slide content:** ER diagram (from Section 3.3). Highlight the `status` field on `listings` and the `referenceNumber` on `reservations`.
**Voice-over:** "Four tables: users, listings, reservations, and user_preferences. The status field on listings is the key to preventing double-booking. The referenceNumber on reservations gives students a unique receipt."

---

### Scene 6 — Live Demo: Registration & Login (3:45 – 5:00)
**Live app:** Open the app on emulator or device.
- Show the Register screen — select Student role, fill in fields, tap Create Account
- Show the conditional Student ID field disappearing when Provider is selected
- Log out, then log back in as a student
**Voice-over:** "Registration supports two roles. Students must provide a Student ID. Passwords are hashed before storage — never saved in plain text."

---

### Scene 7 — Live Demo: Listings & Detail (5:00 – 6:15)
**Live app:**
- Scroll through the listings list — point out the distance badge and status badge on each card
- Tap a listing to open the detail view
- Point out all fields: rent, deposit, type, availability date, amenities
**Voice-over:** "Every listing shows the distance from UB Main Campus, calculated using the Haversine formula. The detail view shows all required fields including the deposit amount upfront."

---

### Scene 8 — Live Demo: Smart Filtering & Notifications (6:15 – 7:15)
**Live app:**
- Tap the filter icon
- Set a price range and location, tap Apply Filters — show the list updating
- Tap "Save to My Preferences"
**Slide content:** Diagram of the WorkManager notification flow.
**Voice-over:** "Filters apply instantly. Saving preferences registers them with WorkManager, which runs every 15 minutes in the background. When a new listing matches your saved criteria, you receive a local notification."

---

### Scene 9 — Live Demo: Reservation & Receipt (7:15 – 8:30)
**Live app:**
- Tap Reserve on an Available listing
- Fill in the mock card details
- Tap Pay — show the confirmation dialog — confirm
- Show the receipt screen with the reference number
- Navigate back — show the listing now shows "Reserved" badge
**Voice-over:** "The payment is simulated — no real card processing. On confirmation, a unique reference number is generated and the listing is locked. Any other student who tries to reserve the same room will see it marked Reserved."

---

### Scene 10 — Live Demo: Route Map (8:30 – 9:15)
**Live app:**
- Open a listing detail, tap the Route button
- Show the Google Map with two markers and the polyline
- Point to the campus marker and the listing marker
**Voice-over:** "The extension feature shows a route from UB Main Campus to the listing on Google Maps. The distance shown on the card is calculated with the Haversine formula using the coordinates of each Gaborone area."

---

### Scene 11 — Conclusion (9:15 – 10:00)
**Slide content:** Mark summary table (from REPORT.md). Bullet list of what was achieved.
**Voice-over:** "All five features from the assignment brief have been implemented: User Management, Listings with images, Smart Filtering with local notifications, Deposit and Reservation with receipt generation, and the Campus Distance extension feature. The app is built entirely with local storage — no internet connection is required beyond the Google Maps tile loading."

---

## 6. References

1. Android Developers — Jetpack Compose: https://developer.android.com/jetpack/compose
2. Android Developers — Room Persistence Library: https://developer.android.com/training/data-storage/room
3. Android Developers — WorkManager: https://developer.android.com/topic/libraries/architecture/workmanager
4. Coil Image Loading for Android: https://coil-kt.github.io/coil/
5. Google Maps Platform — Maps Compose: https://developers.google.com/maps/documentation/android-sdk/maps-compose
6. Material Design 3: https://m3.material.io/
