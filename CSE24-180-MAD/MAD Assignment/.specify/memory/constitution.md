# Student Accommodation Finder Constitution

## Core Principles

### I. Feature Completeness (NON-NEGOTIABLE)
All five core features must be fully functional with no critical bugs:
- User Management: Registration, login, role-based access for 50+ users
- Listings: Complete CRUD with all required fields, images, 50+ records
- Smart Filtering & Alerts: Price, location, date filters + local notifications
- Deposit & Reservation: Simulated payment, receipt generation, status management
- Extension Feature: chat system, fully integrated

### II. Data Requirements
Minimum data thresholds are mandatory:
- 50+ student accounts stored in local database
- 50+ house listings with complete information
- All records must be retrievable and persistent across sessions
- Proper database schema with relationships (User, Listing, Reservation, UserPreferences)

### III. Required Fields Standard
Every entity must include all specified fields:
- **User**: email, password, studentId, phone, role (Student/Provider)
- **Listing**: title, price (BWP), location (Gaborone areas), type, amenities, availabilityDate, deposit, image(s), status
- **Reservation**: listingId, studentId, referenceNumber, amount, date, status
- Missing fields result in feature incompleteness

### IV. Role-Based Access Control
Proper permission enforcement between roles:
- **Student Role**: View listings, apply filters, save preferences, make reservations, receive notifications
- **Provider Role**: Create listings, manage own listings, view reservations
- Conditional UI based on active role
- No unauthorized access to role-specific features

### V. Image Handling Requirement
Every listing must support image functionality:
- At least one image per listing (mandatory)
- Image upload from camera or gallery
- Proper image display in list and detail views
- Image compression for storage efficiency
- Placeholder handling for edge cases

### VI. Reservation Logic Integrity
Prevent double-booking and maintain data consistency:
- Listing status changes to "Reserved" after successful payment
- Reserved listings cannot be booked by other users
- Status updates persist in database
- Clear visual indicators for availability status
- Proper error handling for reservation conflicts

### VII. Local Notification System
Smart alerts based on user preferences:
- Users can save preferences (price range, location, availability date)
- Background process checks new listings against saved preferences
- Local notification triggered when match found
- Notification displays listing summary
- Tapping notification navigates to listing detail

## Technical Standards

### Database Architecture
- Local database: Room DB (Android)
- Proper schema design with foreign key relationships
- CRUD operations for all entities
- Data validation at database layer
- Migration strategy for schema changes

### Security Requirements
- Password hashing: bcrypt (cost factor ≥ 10) — no plain text, no SHA-256
- Session management with secure tokens
- Input sanitization to prevent injection
- Role verification on all protected operations
- Secure local storage for sensitive data

### Validation Rules
- **Email**: Valid format, unique constraint
- **Password**: Minimum 8 characters, alphanumeric + special char
- **Student ID**: Required, unique, alphanumeric
- **Phone**: Valid format (Botswana: +267 or 7/8 digits)
- **Price/Deposit**: Positive numbers, BWP currency
- **Availability Date**: Future date only
- **Location**: Must be valid Gaborone area

### UI/UX Standards
- Material Design (Android) 
- Consistent navigation pattern (bottom nav/drawer)
- Loading states for all async operations
- Error messages: user-friendly, actionable
- Input validation with real-time visual feedback
- Empty states for lists with helpful messaging
- Responsive layouts for different screen sizes

## Grading Compliance

### A. User Management (20 Marks)
- **Student Registration (8 marks)**: Full validation, data storage, 50+ users support
- **Login Authentication (6 marks)**: Functional login, error handling, session management
- **Role-Based Access (6 marks)**: Proper role management with controlled permissions

### B. Listings (25 Marks)
- **Listing Creation (8 marks)**: Complete form with all required fields
- **Listing Information (7 marks)**: All fields displayed correctly
- **Image Handling (5 marks)**: Upload/display implemented correctly
- **Data Storage (5 marks)**: 50+ listings stored and retrievable

### C. Smart Filtering & Alerts (20 Marks)
- **Price Filtering (6 marks)**: Accurate filtering by price range
- **Location Filtering (5 marks)**: Filtering by Gaborone areas
- **Availability Date Filtering (5 marks)**: Accurate date-based filtering
- **Smart Alerts (4 marks)**: Local notifications triggered on preference match

### D. Deposit and Reservation (15 Marks)
- **Payment Simulation (5 marks)**: Clear simulated workflow
- **Receipt/Reference (5 marks)**: Generated after reservation with all details
- **Reservation Logic (5 marks)**: Status change + duplicate prevention

### E. Extension Feature (20 Marks)
- **Implementation (10 marks)**: Fully functional feature integrated in system
- **Integration (5 marks)**: Works smoothly with main application
- **Usability & Design (5 marks)**: Enhances UX with professional design

## Development Workflow

### Phase 1: Foundation (Week 1)
- Setup project structure and database
- Implement User Management (registration, login, roles)
- Create basic Listings CRUD
- Seed database with initial test data

### Phase 2: Core Features (Week 2)
- Complete Listings with image handling
- Implement all three filters (price, location, date)
- Setup local notification system
- Reach 50+ users and 50+ listings threshold

### Phase 3: Transactions (Week 3)
- Build payment simulation workflow
- Implement receipt generation
- Add reservation logic with status management
- Implement chosen extension feature

### Phase 4: Polish & Testing (Week 4)
- Integration testing of all features
- UI/UX refinement
- Bug fixes and edge case handling
- Final data population and verification

## Quality Gates

### Pre-Submission Checklist
- [ ] 50+ users stored and retrievable
- [ ] 50+ listings stored with all required fields
- [ ] All listings have at least one image
- [ ] Registration and login fully functional
- [ ] Role-based access properly enforced
- [ ] All three filters working accurately
- [ ] Local notifications triggering on preference match
- [ ] Payment simulation complete with receipt
- [ ] Reservation changes status to "Reserved"
- [ ] Reserved listings cannot be double-booked
- [ ] Extension feature fully integrated
- [ ] No critical bugs or crashes
- [ ] All validation rules implemented
- [ ] Error handling throughout application

### Testing Requirements
- **Unit Tests**: Authentication logic, filter algorithms, validation functions
- **Integration Tests**: Database operations, image upload/retrieval, notification triggering
- **User Acceptance Tests**: Complete user journeys for Student and Provider roles

## Prohibited Practices

- Hardcoded credentials or API keys in source code
- Plain text password storage
- Missing input validation on user inputs
- Incomplete error handling (silent failures)
- Fewer than 50 users or 50 listings
- Missing required fields in any entity
- Non-functional features marked as complete
- Real payment processing (must be simulated only)
- Plagiarized code without proper attribution

## Excellence Criteria

To achieve 90-100% (Excellent grade):
- All features fully functional with comprehensive error handling
- All required fields present, validated, and displayed correctly
- 50+ users and 50+ listings with complete data
- Images working flawlessly for all listings
- All filters accurate with real-time results
- Local notifications reliably triggered
- Complete payment workflow with professional receipt
- Reservation logic preventing all edge cases
- Extension feature seamlessly integrated and enhancing UX
- Professional, intuitive UI/UX throughout
- Clean, maintainable code with proper architecture
- Comprehensive testing coverage

## Governance

This constitution defines the mandatory requirements for the Student Accommodation Finder mobile application. All features must comply with the standards outlined herein to achieve passing grade.

### Amendment Process
- Constitution changes require documentation of rationale
- Version number incremented on amendments
- All team members notified of changes

### Compliance Verification
- All code reviews must verify constitutional compliance
- Feature completion requires checklist verification
- Pre-submission audit against quality gates mandatory

**Version**: 1.0.1 | **Ratified**: 2026-03-13 | **Last Amended**: 2026-03-30
