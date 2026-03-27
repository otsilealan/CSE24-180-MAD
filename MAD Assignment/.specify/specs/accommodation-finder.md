# Feature Specification: Student Accommodation Finder

**Feature Branch**: `main`  
**Created**: 2026-03-13  
**Status**: Baseline  
**Input**: Mobile app for students to find and reserve accommodation in Gaborone, Botswana

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Student Registration & Login (Priority: P1)

Students can create accounts and log in to access the accommodation finder platform.

**Why this priority**: Foundation for all other features. Without authentication, no user-specific functionality is possible.

**Independent Test**: Can be fully tested by registering a new student account, logging out, and logging back in. Delivers immediate value by establishing user identity and session management.

**Acceptance Scenarios**:

1. **Given** I am a new student, **When** I provide email, password, student ID, and phone number, **Then** my account is created and I can log in
2. **Given** I am a registered student, **When** I enter correct credentials, **Then** I am logged into the app
3. **Given** I am a registered student, **When** I enter incorrect credentials, **Then** I see an error message and cannot access the app
4. **Given** I am logged in, **When** I close and reopen the app, **Then** I remain logged in (session persists)

---

### User Story 2 - Browse Available Listings (Priority: P1)

Students can view all available accommodation listings with complete information to make informed decisions.

**Why this priority**: Core value proposition. Students need to see what's available before any other interaction.

**Independent Test**: Can be fully tested by logging in and viewing the list of accommodations. Delivers value by showing available options with prices, locations, and amenities.

**Acceptance Scenarios**:

1. **Given** I am logged in as a student, **When** I open the listings page, **Then** I see all available accommodations with title, price, location, and image
2. **Given** I am viewing listings, **When** I tap on a listing, **Then** I see full details including type, amenities, availability date, and deposit amount
3. **Given** there are 50+ listings in the system, **When** I scroll through the list, **Then** all listings load and display correctly
4. **Given** I am viewing a listing, **When** the listing is reserved, **Then** I see a "Reserved" status indicator

---

### User Story 3 - Filter Listings by Preferences (Priority: P2)

Students can filter listings by price range, location, and availability date to find suitable accommodation quickly.

**Why this priority**: Enhances usability significantly. With 50+ listings, filtering is essential for efficient searching.

**Independent Test**: Can be fully tested by applying different filter combinations and verifying results match criteria. Delivers value by reducing search time.

**Acceptance Scenarios**:

1. **Given** I am viewing listings, **When** I set a price range (min/max BWP), **Then** only listings within that range are displayed
2. **Given** I am viewing listings, **When** I select specific Gaborone areas, **Then** only listings in those areas are displayed
3. **Given** I am viewing listings, **When** I select an availability date, **Then** only listings available on or after that date are displayed
4. **Given** I have applied multiple filters, **When** I clear filters, **Then** all listings are displayed again

---

### User Story 4 - Save Preferences & Receive Alerts (Priority: P2)

Students can save their accommodation preferences and receive notifications when new matching listings are added.

**Why this priority**: Proactive feature that keeps students informed without constant manual checking.

**Independent Test**: Can be fully tested by saving preferences, adding a matching listing (as provider), and verifying notification is received. Delivers value through automated matching.

**Acceptance Scenarios**:

1. **Given** I am a student, **When** I save my preferences (price, location, date), **Then** my preferences are stored
2. **Given** I have saved preferences, **When** a new listing matches my criteria, **Then** I receive a local notification
3. **Given** I receive a notification, **When** I tap on it, **Then** I am taken to the matching listing details
4. **Given** I have saved preferences, **When** I update them, **Then** future notifications use the new criteria

---

### User Story 5 - Reserve Accommodation with Deposit (Priority: P1)

Students can reserve accommodation by completing a simulated payment for the deposit amount.

**Why this priority**: Core transaction feature. Completes the booking journey and prevents double-booking.

**Independent Test**: Can be fully tested by selecting a listing, completing payment simulation, and verifying reservation status changes. Delivers value by securing accommodation.

**Acceptance Scenarios**:

1. **Given** I am viewing an available listing, **When** I initiate reservation, **Then** I see the payment screen with deposit amount
2. **Given** I am on the payment screen, **When** I complete the simulated payment, **Then** I receive a receipt with a unique reference number
3. **Given** I have completed payment, **When** I view the listing again, **Then** it shows "Reserved" status
4. **Given** a listing is reserved, **When** another student tries to reserve it, **Then** they cannot proceed with payment
5. **Given** I have made a reservation, **When** I view my receipt, **Then** it shows listing details, amount paid, reference number, and date

---

### User Story 6 - Provider Creates Listings (Priority: P1)

Accommodation providers can create listings with all required information and images.

**Why this priority**: Supply side of marketplace. Without listings, students have nothing to browse.

**Independent Test**: Can be fully tested by registering as provider, creating a listing with all fields, and verifying it appears in student view. Delivers value by populating the marketplace.

**Acceptance Scenarios**:

1. **Given** I am logged in as a provider, **When** I access the create listing form, **Then** I can enter title, price, location, type, amenities, availability date, deposit, and upload image
2. **Given** I am creating a listing, **When** I submit with all required fields, **Then** the listing is saved and visible to students
3. **Given** I am creating a listing, **When** I try to submit without required fields, **Then** I see validation errors
4. **Given** I am creating a listing, **When** I upload an image from camera or gallery, **Then** the image is stored and displayed with the listing

---

### User Story 7 - Campus Distance & Navigation (Priority: P3)

Students can see distance from campus to each listing and view route directions.

**Why this priority**: Enhancement feature that aids decision-making but not critical for core functionality.

**Independent Test**: Can be fully tested by viewing listings with distance indicators and opening map for directions. Delivers value by helping students assess commute feasibility.

**Acceptance Scenarios**:

1. **Given** I am viewing listings, **When** I see a listing card, **Then** I see the distance from campus displayed
2. **Given** I am viewing listing details, **When** I tap "View Route", **Then** I see a map with directions from campus to the accommodation
3. **Given** I am viewing the map, **When** I interact with it, **Then** I can see turn-by-turn directions

---

### Edge Cases

- What happens when a student tries to register with an email that already exists?
- How does the system handle image upload failures or corrupted images?
- What happens when a student tries to reserve a listing that was just reserved by another user?
- How does the system handle notifications when the app is closed or in background?
- What happens when filter criteria match zero listings?
- How does the system handle invalid or expired availability dates?
- What happens when a provider tries to create a listing without uploading an image?
- How does the system handle concurrent reservation attempts on the same listing?

## Requirements *(mandatory)*

### Functional Requirements

**User Management**
- **FR-001**: System MUST support student registration with email, password, student ID, and phone number
- **FR-002**: System MUST validate email format and enforce unique email constraint
- **FR-003**: System MUST hash passwords before storage (no plain text)
- **FR-004**: System MUST support login authentication with email and password
- **FR-005**: System MUST maintain user sessions across app restarts
- **FR-006**: System MUST support role-based access (Student and Provider roles)
- **FR-007**: System MUST store minimum 50 student accounts

**Listings Management**
- **FR-008**: System MUST allow providers to create listings with title, price (BWP), location, type, amenities, availability date, deposit, and image
- **FR-009**: System MUST validate all required fields before saving a listing
- **FR-010**: System MUST store minimum 50 house listings
- **FR-011**: System MUST require at least one image per listing
- **FR-012**: System MUST display all listing fields in list and detail views
- **FR-013**: System MUST support image upload from camera and gallery
- **FR-014**: System MUST persist listing data across app sessions

**Filtering & Alerts**
- **FR-015**: System MUST filter listings by price range (min/max BWP)
- **FR-016**: System MUST filter listings by Gaborone area locations
- **FR-017**: System MUST filter listings by availability date
- **FR-018**: System MUST allow students to save filter preferences
- **FR-019**: System MUST trigger local notifications when new listings match saved preferences
- **FR-020**: System MUST check for preference matches in background

**Reservations**
- **FR-021**: System MUST provide simulated payment workflow with deposit amount display
- **FR-022**: System MUST generate unique reference number for each reservation
- **FR-023**: System MUST display receipt with listing info, amount, reference number, and date
- **FR-024**: System MUST change listing status to "Reserved" after successful payment
- **FR-025**: System MUST prevent reservation of already-reserved listings
- **FR-026**: System MUST persist reservation data

**Extension Feature (Campus Navigation)**
- **FR-027**: System MUST calculate and display distance from campus to each listing
- **FR-028**: System MUST integrate map view for route visualization
- **FR-029**: System MUST show turn-by-turn directions from campus to accommodation

### Key Entities

- **User**: Represents students and providers with authentication credentials (email, password), profile info (studentId, phone), and role designation
- **Listing**: Represents accommodation with descriptive info (title, type), pricing (price, deposit), location (Gaborone area), features (amenities), temporal data (availabilityDate), visual (images), and status (Available/Reserved). Related to User (provider)
- **Reservation**: Represents booking transaction with unique identifier (referenceNumber), financial data (amount), temporal data (reservationDate), and relationships to Listing and User (student)
- **UserPreferences**: Represents saved filter criteria with price range (minPrice, maxPrice), location preferences, and availability date. Related to User

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Students can complete registration and login in under 1 minute
- **SC-002**: System stores and retrieves 50+ users and 50+ listings without performance degradation
- **SC-003**: Students can find suitable accommodation using filters in under 2 minutes
- **SC-004**: 100% of reservations successfully change listing status and prevent duplicate bookings
- **SC-005**: Local notifications are delivered within 1 minute of matching listing creation
- **SC-006**: All images upload and display correctly for 100% of listings
- **SC-007**: Students can complete the entire journey (browse → filter → reserve) without errors
- **SC-008**: Providers can create a complete listing with image in under 3 minutes
- **SC-009**: Distance calculations are accurate within 5% margin of error
- **SC-010**: Zero critical bugs or crashes during user acceptance testing
