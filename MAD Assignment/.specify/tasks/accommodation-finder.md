# Tasks: Student Accommodation Finder

**Input**: Design documents from `.specify/specs/` and `.specify/plans/`
**Prerequisites**: constitution.md, accommodation-finder.md (spec), accommodation-finder.md (plan)

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1-US7)

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create Android project with Kotlin and Jetpack Compose in Android Studio
- [ ] T002 Add dependencies to build.gradle.kts: Room, Coil, WorkManager, Google Maps SDK
- [ ] T003 [P] Configure AndroidManifest.xml with permissions (INTERNET, CAMERA, NOTIFICATIONS, LOCATION)
- [ ] T004 [P] Setup project package structure: data/, ui/, workers/, utils/

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Create User entity in app/src/main/java/com/accommodation/data/database/entities/User.kt
- [ ] T006 Create Listing entity in app/src/main/java/com/accommodation/data/database/entities/Listing.kt
- [ ] T007 Create Reservation entity in app/src/main/java/com/accommodation/data/database/entities/Reservation.kt
- [ ] T008 Create UserPreferences entity in app/src/main/java/com/accommodation/data/database/entities/UserPreferences.kt
- [ ] T009 Create UserDao in app/src/main/java/com/accommodation/data/database/dao/UserDao.kt
- [ ] T010 Create ListingDao in app/src/main/java/com/accommodation/data/database/dao/ListingDao.kt
- [ ] T011 Create ReservationDao in app/src/main/java/com/accommodation/data/database/dao/ReservationDao.kt
- [ ] T012 Create PreferencesDao in app/src/main/java/com/accommodation/data/database/dao/PreferencesDao.kt
- [ ] T013 Create AppDatabase in app/src/main/java/com/accommodation/data/database/AppDatabase.kt
- [ ] T014 Create seed data script with 50+ users and 50+ listings in app/src/main/java/com/accommodation/data/SeedData.kt
- [ ] T015 Create SessionManager utility in app/src/main/java/com/accommodation/utils/SessionManager.kt
- [ ] T016 [P] Create ValidationUtils in app/src/main/java/com/accommodation/utils/ValidationUtils.kt
- [ ] T017 [P] Create ImageUtils in app/src/main/java/com/accommodation/utils/ImageUtils.kt

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Student Registration & Login (Priority: P1) 🎯 MVP

**Goal**: Students can create accounts and log in to access the platform

**Independent Test**: Register new account, logout, login with credentials, verify session persists

### Implementation for User Story 1

- [ ] T018 [P] [US1] Create UserRepository in app/src/main/java/com/accommodation/data/repository/UserRepository.kt
- [ ] T019 [P] [US1] Create AuthViewModel in app/src/main/java/com/accommodation/ui/auth/AuthViewModel.kt
- [ ] T020 [US1] Implement RegisterScreen in app/src/main/java/com/accommodation/ui/auth/RegisterScreen.kt
- [ ] T021 [US1] Add email validation logic in ValidationUtils
- [ ] T022 [US1] Add password hashing (SHA-256) in AuthViewModel
- [ ] T023 [US1] Implement LoginScreen in app/src/main/java/com/accommodation/ui/auth/LoginScreen.kt
- [ ] T024 [US1] Add session persistence logic in SessionManager
- [ ] T025 [US1] Implement role selection UI (Student/Provider) in RegisterScreen
- [ ] T026 [US1] Add authentication error handling and user feedback

**Checkpoint**: Registration and login fully functional, session persists, 50+ users can be created

---

## Phase 4: User Story 6 - Provider Creates Listings (Priority: P1)

**Goal**: Providers can create listings with all required fields and images

**Independent Test**: Login as provider, create listing with all fields and image, verify it saves and displays

### Implementation for User Story 6

- [ ] T027 [P] [US6] Create ListingRepository in app/src/main/java/com/accommodation/data/repository/ListingRepository.kt
- [ ] T028 [P] [US6] Create ListingsViewModel in app/src/main/java/com/accommodation/ui/listings/ListingsViewModel.kt
- [ ] T029 [US6] Implement CreateListingScreen in app/src/main/java/com/accommodation/ui/listings/CreateListingScreen.kt
- [ ] T030 [US6] Add form fields: title, price, location dropdown, type, amenities checkboxes
- [ ] T031 [US6] Add availability date picker to CreateListingScreen
- [ ] T032 [US6] Add deposit amount field with validation
- [ ] T033 [US6] Implement image picker (camera/gallery) in CreateListingScreen
- [ ] T034 [US6] Add image compression logic in ImageUtils
- [ ] T035 [US6] Implement image storage and retrieval in ListingRepository
- [ ] T036 [US6] Add form validation before submission
- [ ] T037 [US6] Restrict CreateListingScreen to Provider role only

**Checkpoint**: Providers can create complete listings with images, 50+ listings can be stored

---

## Phase 5: User Story 2 - Browse Available Listings (Priority: P1)

**Goal**: Students can view all available accommodation listings with complete information

**Independent Test**: Login as student, view listings list, tap listing to see details, verify all fields display

### Implementation for User Story 2

- [ ] T038 [US2] Implement ListingsScreen with LazyColumn in app/src/main/java/com/accommodation/ui/listings/ListingsScreen.kt
- [ ] T039 [US2] Create listing card component with title, price, location, image
- [ ] T040 [US2] Add Coil image loading to listing cards
- [ ] T041 [US2] Implement ListingDetailScreen in app/src/main/java/com/accommodation/ui/listings/ListingDetailScreen.kt
- [ ] T042 [US2] Display all fields in detail view: type, amenities, availability, deposit
- [ ] T043 [US2] Add status indicator (Available/Reserved) to listing cards and detail
- [ ] T044 [US2] Implement navigation from list to detail screen
- [ ] T045 [US2] Add loading states and error handling

**Checkpoint**: All listings display correctly with images, detail view shows complete information

---

## Phase 6: User Story 3 - Filter Listings by Preferences (Priority: P2)

**Goal**: Students can filter listings by price, location, and availability date

**Independent Test**: Apply price filter, verify results match range; apply location filter, verify results; apply date filter, verify results

### Implementation for User Story 3

- [ ] T046 [P] [US3] Create FilterViewModel in app/src/main/java/com/accommodation/ui/filter/FilterViewModel.kt
- [ ] T047 [US3] Implement FilterScreen in app/src/main/java/com/accommodation/ui/filter/FilterScreen.kt
- [ ] T048 [US3] Add price range slider (min/max BWP) to FilterScreen
- [ ] T049 [US3] Add location multi-select dropdown (Gaborone areas) to FilterScreen
- [ ] T050 [US3] Add availability date picker to FilterScreen
- [ ] T051 [US3] Implement filter logic in ListingRepository (price range query)
- [ ] T052 [US3] Implement location filter logic in ListingRepository
- [ ] T053 [US3] Implement availability date filter logic in ListingRepository
- [ ] T054 [US3] Add "Apply Filters" and "Clear Filters" buttons
- [ ] T055 [US3] Update ListingsScreen to display filtered results

**Checkpoint**: All three filters work accurately, results update in real-time

---

## Phase 7: User Story 4 - Save Preferences & Receive Alerts (Priority: P2)

**Goal**: Students can save preferences and receive notifications for matching listings

**Independent Test**: Save preferences, add matching listing as provider, verify notification received, tap notification to open listing

### Implementation for User Story 4

- [ ] T056 [P] [US4] Create PreferencesRepository in app/src/main/java/com/accommodation/data/repository/PreferencesRepository.kt
- [ ] T057 [US4] Add "Save Preferences" button to FilterScreen
- [ ] T058 [US4] Implement save preferences logic in FilterViewModel
- [ ] T059 [US4] Create NotificationWorker in app/src/main/java/com/accommodation/workers/NotificationWorker.kt
- [ ] T060 [US4] Implement preference matching algorithm in NotificationWorker
- [ ] T061 [US4] Setup notification channel in MainActivity
- [ ] T062 [US4] Implement notification creation with listing summary
- [ ] T063 [US4] Add deep link to open listing detail from notification
- [ ] T064 [US4] Schedule periodic WorkManager task (every 15 minutes)
- [ ] T065 [US4] Test notification triggering with matching listing

**Checkpoint**: Preferences save correctly, notifications trigger on match, tapping opens listing

---

## Phase 8: User Story 5 - Reserve Accommodation with Deposit (Priority: P1)

**Goal**: Students can reserve accommodation through simulated payment

**Independent Test**: Select available listing, complete payment, verify receipt generated, verify status changes to Reserved, verify cannot be booked again

### Implementation for User Story 5

- [ ] T066 [P] [US5] Create ReservationRepository in app/src/main/java/com/accommodation/data/repository/ReservationRepository.kt
- [ ] T067 [P] [US5] Create ReservationViewModel in app/src/main/java/com/accommodation/ui/reservation/ReservationViewModel.kt
- [ ] T068 [US5] Implement PaymentScreen in app/src/main/java/com/accommodation/ui/reservation/PaymentScreen.kt
- [ ] T069 [US5] Display listing details and deposit amount on PaymentScreen
- [ ] T070 [US5] Add mock payment form (card number, expiry, CVV fields)
- [ ] T071 [US5] Add payment confirmation dialog
- [ ] T072 [US5] Generate unique reference number (UUID) in ReservationViewModel
- [ ] T073 [US5] Implement ReceiptScreen in app/src/main/java/com/accommodation/ui/reservation/ReceiptScreen.kt
- [ ] T074 [US5] Display receipt with listing info, amount, reference, date
- [ ] T075 [US5] Update listing status to "Reserved" in database after payment
- [ ] T076 [US5] Add duplicate booking prevention check in ReservationRepository
- [ ] T077 [US5] Hide "Reserve" button for reserved listings
- [ ] T078 [US5] Add error handling for reservation conflicts

**Checkpoint**: Payment flow completes, receipt displays, status changes, duplicate bookings prevented

---

## Phase 9: User Story 7 - Campus Distance & Navigation (Priority: P3)

**Goal**: Students can see distance from campus and view route directions

**Independent Test**: View listings with distance displayed, tap "View Route", verify map shows directions from campus

### Implementation for User Story 7

- [ ] T079 [P] [US7] Create NavigationViewModel in app/src/main/java/com/accommodation/ui/navigation/NavigationViewModel.kt
- [ ] T080 [US7] Add campus location constant (latitude/longitude) in NavigationViewModel
- [ ] T081 [US7] Implement Haversine distance formula in NavigationViewModel
- [ ] T082 [US7] Calculate distance for each listing in ListingsViewModel
- [ ] T083 [US7] Display distance on listing cards in ListingsScreen
- [ ] T084 [US7] Add Google Maps API key to AndroidManifest.xml
- [ ] T085 [US7] Implement MapScreen in app/src/main/java/com/accommodation/ui/navigation/MapScreen.kt
- [ ] T086 [US7] Add "View Route" button to ListingDetailScreen
- [ ] T087 [US7] Display route from campus to listing on MapScreen
- [ ] T088 [US7] Add turn-by-turn directions display

**Checkpoint**: Distance displays on all listings, map shows accurate routes with directions

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T089 [P] Add loading states to all screens
- [ ] T090 [P] Add error messages for all failure scenarios
- [ ] T091 [P] Add empty states for listings (no results, no saved preferences)
- [ ] T092 [P] Implement bottom navigation (Listings, Filter, Profile)
- [ ] T093 Add Material Design theming and consistent styling
- [ ] T094 Optimize image loading and caching with Coil
- [ ] T095 Add input validation feedback (real-time error display)
- [ ] T096 Test all edge cases from specification
- [ ] T097 Verify 50+ users and 50+ listings in database
- [ ] T098 Performance testing (load time, filter response, UI smoothness)
- [ ] T099 Final bug fixes and UI polish
- [ ] T100 Create APK for submission

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - start immediately
- **Foundational (Phase 2)**: Depends on Setup - BLOCKS all user stories
- **User Stories (Phase 3-9)**: All depend on Foundational completion
  - US1 (Registration/Login) → Must complete first (authentication required)
  - US6 (Create Listings) → Can start after US1 (needs authentication)
  - US2 (Browse Listings) → Can start after US6 (needs listings to display)
  - US3 (Filter) → Can start after US2 (filters existing listings)
  - US4 (Alerts) → Can start after US3 (uses saved filter preferences)
  - US5 (Reservations) → Can start after US2 (reserves existing listings)
  - US7 (Navigation) → Can start after US2 (adds to existing listings)
- **Polish (Phase 10)**: Depends on all user stories

### Critical Path

1. Setup → Foundational → US1 → US6 → US2 → US5 (Core booking flow)
2. US3 → US4 (Filtering enhancement)
3. US7 (Extension feature)
4. Polish

### Parallel Opportunities

- Within Foundational: T005-T008 (entities), T009-T012 (DAOs), T016-T017 (utils)
- Within US1: T018-T019 (repository and viewmodel)
- Within US6: T027-T028 (repository and viewmodel)
- Within US7: T079-T080 (viewmodel setup)
- Within Polish: T089-T092 (UI improvements)

---

## Implementation Strategy

### Week 1: Foundation + Authentication + Listings
- Days 1-2: Phase 1 (Setup) + Phase 2 (Foundational)
- Days 3-4: Phase 3 (US1 - Registration/Login)
- Days 5-7: Phase 4 (US6 - Create Listings) + Phase 5 (US2 - Browse Listings)

### Week 2: Filtering + Reservations + Extension
- Days 1-2: Phase 6 (US3 - Filtering) + Phase 7 (US4 - Alerts)
- Days 3-4: Phase 8 (US5 - Reservations)
- Days 5-6: Phase 9 (US7 - Navigation)
- Day 7: Phase 10 (Polish)

---

## Notes

- [P] tasks can run in parallel (different files)
- [Story] label maps task to user story for traceability
- Commit after each task or logical group
- Test each user story independently at checkpoint
- Verify all grading criteria met before submission
- Total: 100 tasks for 100 marks
