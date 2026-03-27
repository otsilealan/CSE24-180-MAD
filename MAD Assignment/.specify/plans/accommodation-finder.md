# Implementation Plan: Student Accommodation Finder

**Branch**: `main` | **Date**: 2026-03-13 | **Spec**: [accommodation-finder.md](../specs/accommodation-finder.md)

## Summary

Mobile application for students to find and reserve accommodation in Gaborone, Botswana. Core features: user management with role-based access, listings with images, smart filtering with notifications, simulated payment with reservations, and campus navigation. Technical approach: Android native with Kotlin, Room database for local storage, WorkManager for background notifications, Google Maps SDK for navigation.

## Technical Context

**Language/Version**: Kotlin 1.9+ / Android SDK 24+ (Android 7.0+)  
**Primary Dependencies**: Room Database, Jetpack Compose, ViewModel, Coil (image loading), WorkManager, Google Maps SDK  
**Storage**: Room Database (SQLite) for local persistence  
**Testing**: JUnit 4, Espresso (UI tests), Room testing utilities  
**Target Platform**: Android 7.0+ (API 24+)  
**Project Type**: Mobile app (Android)  
**Performance Goals**: <2s listing load time, <500ms filter response, smooth 60fps UI  
**Constraints**: Offline-capable for browsing cached listings, <100MB app size, local-only data (no backend)  
**Scale/Scope**: 50+ users, 50+ listings, 5 core features, 15-20 screens

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- ✅ Feature Completeness: All 5 features planned (User Management, Listings, Filtering, Reservations, Navigation)
- ✅ Data Requirements: Database schema supports 50+ users and 50+ listings
- ✅ Required Fields: All entity fields defined in spec
- ✅ Role-Based Access: Student/Provider roles with permission enforcement
- ✅ Image Handling: Image upload/display planned for all listings
- ✅ Reservation Logic: Status management and duplicate prevention designed
- ✅ Local Notifications: WorkManager background task for preference matching

## Project Structure

### Documentation (this feature)

```text
.specify/
├── memory/
│   └── constitution.md
├── specs/
│   └── accommodation-finder.md
├── plans/
│   └── accommodation-finder.md    # This file
└── templates/
```

### Source Code (repository root)

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/accommodation/
│   │   │   ├── data/
│   │   │   │   ├── database/
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── UserDao.kt
│   │   │   │   │   │   ├── ListingDao.kt
│   │   │   │   │   │   ├── ReservationDao.kt
│   │   │   │   │   │   └── PreferencesDao.kt
│   │   │   │   │   └── entities/
│   │   │   │   │       ├── User.kt
│   │   │   │   │       ├── Listing.kt
│   │   │   │   │       ├── Reservation.kt
│   │   │   │   │       └── UserPreferences.kt
│   │   │   │   └── repository/
│   │   │   │       ├── UserRepository.kt
│   │   │   │       ├── ListingRepository.kt
│   │   │   │       └── ReservationRepository.kt
│   │   │   ├── ui/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   └── AuthViewModel.kt
│   │   │   │   ├── listings/
│   │   │   │   │   ├── ListingsScreen.kt
│   │   │   │   │   ├── ListingDetailScreen.kt
│   │   │   │   │   ├── CreateListingScreen.kt
│   │   │   │   │   └── ListingsViewModel.kt
│   │   │   │   ├── filter/
│   │   │   │   │   ├── FilterScreen.kt
│   │   │   │   │   └── FilterViewModel.kt
│   │   │   │   ├── reservation/
│   │   │   │   │   ├── PaymentScreen.kt
│   │   │   │   │   ├── ReceiptScreen.kt
│   │   │   │   │   └── ReservationViewModel.kt
│   │   │   │   └── navigation/
│   │   │   │       ├── MapScreen.kt
│   │   │   │       └── NavigationViewModel.kt
│   │   │   ├── workers/
│   │   │   │   └── NotificationWorker.kt
│   │   │   ├── utils/
│   │   │   │   ├── SessionManager.kt
│   │   │   │   ├── ImageUtils.kt
│   │   │   │   └── ValidationUtils.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── drawable/
│   │   │   └── values/
│   │   └── AndroidManifest.xml
│   └── test/
│       ├── java/com/accommodation/
│       │   ├── database/
│       │   │   └── DatabaseTest.kt
│       │   ├── repository/
│       │   │   └── RepositoryTest.kt
│       │   └── viewmodel/
│       │       └── ViewModelTest.kt
│       └── androidTest/
│           └── java/com/accommodation/
│               ├── ui/
│               │   └── AuthFlowTest.kt
│               └── integration/
│                   └── ReservationFlowTest.kt
└── build.gradle.kts
```

**Structure Decision**: Android single-module app with MVVM architecture. Data layer (Room database, repositories), UI layer (Compose screens, ViewModels), and utilities. Background workers for notifications. Standard Android project structure for maintainability.

## Implementation Phases

### Phase 0: Project Setup & Database Foundation
**Duration**: 1 day  
**Deliverables**: Project structure, database schema, seed data

**Tasks**:
1. Create Android project with Kotlin and Jetpack Compose
2. Add dependencies: Room, Coil, WorkManager, Google Maps
3. Define database entities (User, Listing, Reservation, UserPreferences)
4. Create DAOs with CRUD operations
5. Implement AppDatabase with migrations
6. Create seed data script (50+ users, 50+ listings)
7. Setup repositories for data access

**Validation**: Database queries return seeded data, all entities persist correctly

---

### Phase 1: User Management (20 marks)
**Duration**: 2 days  
**Deliverables**: Registration, login, role-based access

**Tasks**:
1. Create User entity with validation
2. Implement RegisterScreen with form validation
3. Implement password hashing (SHA-256)
4. Create LoginScreen with authentication
5. Implement SessionManager for session persistence
6. Add role selection (Student/Provider)
7. Implement role-based navigation
8. Create AuthViewModel with state management

**Validation**: 
- Register 50+ users successfully
- Login with correct/incorrect credentials
- Session persists across app restarts
- Role-based UI shows correct screens

---

### Phase 2: Listings Management (25 marks)
**Duration**: 3 days  
**Deliverables**: Create, view, and display listings with images

**Tasks**:
1. Create Listing entity with all required fields
2. Implement ListingsScreen (RecyclerView/LazyColumn)
3. Implement ListingDetailScreen with all fields
4. Create CreateListingScreen (Provider only)
5. Add image picker (camera/gallery)
6. Implement image compression and storage
7. Use Coil for image loading/display
8. Add listing status indicator (Available/Reserved)
9. Create ListingsViewModel with filtering logic

**Validation**:
- 50+ listings stored and displayed
- All fields visible in list and detail views
- Images upload and display correctly
- Provider can create listings, Student cannot

---

### Phase 3: Smart Filtering & Alerts (20 marks)
**Duration**: 2 days  
**Deliverables**: Price, location, date filters + notifications

**Tasks**:
1. Create FilterScreen with price range slider
2. Add location dropdown (Gaborone areas)
3. Add availability date picker
4. Implement filter logic in repository
5. Create UserPreferences entity
6. Implement save preferences functionality
7. Create NotificationWorker with WorkManager
8. Implement preference matching algorithm
9. Setup local notification channel
10. Schedule periodic background checks

**Validation**:
- Filters return accurate results
- Preferences save and persist
- Notification triggers when match found
- Tapping notification opens listing detail

---

### Phase 4: Deposit & Reservation (15 marks)
**Duration**: 2 days  
**Deliverables**: Payment simulation, receipt, status management

**Tasks**:
1. Create Reservation entity
2. Implement PaymentScreen with deposit display
3. Add mock payment form (card fields)
4. Generate unique reference number (UUID)
5. Create ReceiptScreen with all details
6. Implement listing status update to "Reserved"
7. Add duplicate booking prevention logic
8. Create ReservationViewModel
9. Add reservation history view

**Validation**:
- Payment flow completes successfully
- Receipt displays with reference number
- Listing status changes to "Reserved"
- Reserved listings cannot be booked again

---

### Phase 5: Campus Navigation (20 marks)
**Duration**: 2 days  
**Deliverables**: Distance calculation, map integration, directions

**Tasks**:
1. Add campus location constant
2. Implement Haversine distance formula
3. Display distance on listing cards
4. Integrate Google Maps SDK
5. Create MapScreen with route visualization
6. Implement directions API integration
7. Add "View Route" button on listing detail
8. Create NavigationViewModel

**Validation**:
- Distance displays on all listings
- Map opens with correct route
- Directions show from campus to listing

---

### Phase 6: Testing & Polish (15 marks - implicit quality)
**Duration**: 2 days  
**Deliverables**: Bug fixes, UI polish, comprehensive testing

**Tasks**:
1. Write unit tests for ViewModels
2. Write database tests for DAOs
3. Create UI tests for critical flows
4. Test all edge cases from spec
5. Fix identified bugs
6. Polish UI/UX (loading states, errors)
7. Add empty states for lists
8. Optimize performance
9. Final data population verification

**Validation**:
- All tests pass
- No critical bugs
- Smooth user experience
- Meets all success criteria

## Risk Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Image storage exceeds limits | High | Implement compression, limit image size to 1MB |
| Notification not triggering | High | Test WorkManager thoroughly, add logging |
| Concurrent reservation conflicts | Medium | Use database transactions, add status checks |
| Google Maps API quota | Low | Use free tier, cache routes |
| Performance with 50+ listings | Medium | Implement pagination, lazy loading |

## Dependencies & Prerequisites

**Required**:
- Android Studio Hedgehog or later
- Android SDK 24+ (API level 24)
- Google Maps API key (free tier)
- Kotlin 1.9+

**Optional**:
- Physical Android device for testing (camera, notifications)
- Emulator with Google Play Services

## Success Metrics

- All 5 features fully functional
- 50+ users and 50+ listings in database
- All grading criteria met (100 marks possible)
- Zero critical bugs in user acceptance testing
- App size <100MB
- Smooth performance (60fps UI, <2s load times)
