# Student Accommodation Finder App - Implementation Plan

## Project Overview
Mobile app for students to find and reserve accommodation in Gaborone, Botswana. Total: 100 marks across 5 features.

## Implementation Plan

### 1. Setup & Data Foundation
- Choose platform (Android with Kotlin recommended, or Flutter for cross-platform)
- Set up local database (Room DB for Android, SQLite for Flutter)
- Create data models: User, Listing, Reservation, UserPreferences
- Seed database with 50+ student accounts and 50+ house listings

### 2. A. User Management (20 marks)
**Priority: High | Estimated: 2-3 days**

- Registration screen with validation (email, password, student ID, phone)
- Login screen with authentication and error handling
- Role selection (Student/Provider) with conditional UI
- Store user sessions (SharedPreferences/DataStore)
- Implement role-based access control throughout app

**Key for "Excellent":** Full validation, proper data storage, support 50+ users, role-based permissions

### 3. B. Listings (25 marks)
**Priority: High | Estimated: 3-4 days**

- Listing creation form (Provider role):
  - Title, price (BWP), location (dropdown: Gaborone areas), type (apartment/room/house)
  - Amenities (checkboxes: WiFi, parking, furnished, etc.)
  - Availability date picker
  - Deposit amount
  - Image picker/camera integration
- Listings display (RecyclerView/ListView) with all fields
- Detail view for each listing
- Store 50+ listings in database with images

**Key for "Excellent":** All fields present, image upload/display working, 50+ records stored

### 4. C. Smart Filtering & Alerts (20 marks)
**Priority: Medium | Estimated: 2-3 days**

- Filter UI with:
  - Price range slider (min/max BWP)
  - Location dropdown (Gaborone areas)
  - Availability date picker
- Apply filters to listing results
- Save user preferences (price range, location, date)
- Local notification system:
  - Check new listings against saved preferences
  - Trigger notification when match found
  - Use WorkManager (Android) or background tasks

**Key for "Excellent":** Accurate filtering on all criteria, local notifications triggered on preference match

### 5. D. Deposit and Reservation (15 marks)
**Priority: Medium | Estimated: 2 days**

- Payment simulation screen:
  - Display listing details and deposit amount
  - Mock payment form (card number, expiry, CVV - not real processing)
  - Confirmation dialog
- Generate unique reference number (UUID or timestamp-based)
- Display receipt with: listing info, amount, reference, date
- Update listing status to "Reserved" in database
- Prevent duplicate reservations (check status before allowing payment)

**Key for "Excellent":** Clear payment workflow, receipt generation, status change to "Reserved", duplicate prevention

### 6. E. Extension Feature (20 marks)
**Priority: Medium | Estimated: 2-3 days**

**Option 1: Campus Distance & Route Navigation** (Recommended - easier)
- Add campus location field to listings
- Calculate distance from campus (Haversine formula or Google Distance Matrix API)
- Display distance on listing cards
- Integrate Google Maps for route visualization
- Show directions from campus to accommodation

**Option 2: Chat System** (More complex)
- Chat screen between student and landlord
- Message storage (local DB with sender/receiver/timestamp)
- Real-time updates (polling or Firebase)
- Chat list showing conversations

**Key for "Excellent":** Fully functional feature, smooth integration, enhances usability

### 7. UI/UX Polish
**Priority: Low | Estimated: 1 day**

- Consistent Material Design/modern UI
- Loading states and error handling
- Empty states for lists
- Input validation feedback
- Navigation flow (bottom nav or drawer)

## Technical Stack Recommendation

### Android (Kotlin):
- Room Database for local storage
- Jetpack Compose or XML layouts
- ViewModel + LiveData/Flow
- WorkManager for notifications
- Coil/Glide for image loading
- Google Maps SDK (if choosing navigation feature)

### Flutter (Dart):
- SQLite/Hive for local storage
- Provider/Riverpod for state management
- flutter_local_notifications
- image_picker for images
- google_maps_flutter (if choosing navigation)

## Development Timeline
- **Week 1:** Setup + User Management + Listings foundation
- **Week 2:** Complete Listings + Smart Filtering
- **Week 3:** Deposit/Reservation + Extension Feature
- **Week 4:** Testing, bug fixes, UI polish

## Critical Success Factors
1. Store and display 50+ users and 50+ listings
2. All required fields present in listings
3. Working image upload/display
4. Functional filtering on price, location, availability
5. Local notifications triggering correctly
6. Payment simulation with receipt generation
7. Reservation status preventing duplicate bookings
8. Extension feature fully integrated

## Grading Rubric Summary

| Feature | Marks | Key Requirements |
|---------|-------|------------------|
| User Management | 20 | Registration, login, role-based access, 50+ users |
| Listings | 25 | All fields, images, 50+ records |
| Smart Filtering & Alerts | 20 | Price/location/date filters, local notifications |
| Deposit & Reservation | 15 | Payment simulation, receipt, status change, duplicate prevention |
| Extension Feature | 20 | Fully functional, well-integrated |
| **Total** | **100** | |
