# Requirements Quality Checklist: Student Accommodation Finder

**Purpose**: Validate completeness, clarity, consistency, and measurability of requirements across all five feature areas
**Created**: 2026-03-30
**Feature**: [accommodation-finder.md](../specs/accommodation-finder.md)

---

## Requirement Completeness

- [ ] CHK001 - Are error handling requirements defined for duplicate email registration? [Completeness, Spec §FR-002]
- [ ] CHK002 - Are requirements specified for what happens when session token expires or becomes invalid? [Gap]
- [ ] CHK003 - Are requirements defined for the provider registration flow (fields, validation, role assignment)? [Gap]
- [ ] CHK004 - Are listing edit and delete requirements specified for providers? [Gap]
- [ ] CHK005 - Are requirements defined for what fields are shown in the listings list view vs. detail view? [Completeness, Spec §FR-012]
- [ ] CHK006 - Are requirements specified for image compression thresholds and maximum file size? [Gap]
- [ ] CHK007 - Are loading state requirements defined for all asynchronous operations (image upload, filter, reservation)? [Gap]
- [ ] CHK008 - Are empty state requirements defined for filtered results returning zero listings? [Completeness, Spec §Edge Cases]
- [ ] CHK009 - Are requirements defined for the notification payload content (what data is shown in the notification)? [Gap]
- [ ] CHK010 - Are requirements specified for the background check frequency/interval for preference matching? [Gap, Spec §FR-020]
- [ ] CHK011 - Are receipt requirements complete — does the spec enumerate all fields that must appear on the receipt? [Completeness, Spec §FR-023]
- [ ] CHK012 - Are requirements defined for reservation cancellation or modification? [Gap]
- [ ] CHK013 - Are distance display requirements specified for the listings list view (format, units, precision)? [Gap, Spec §FR-027]
- [ ] CHK014 - Are requirements defined for what happens when Google Maps is unavailable or the API key is invalid? [Gap]
- [ ] CHK015 - Are logout requirements specified (session clearing, navigation target)? [Gap]

---

## Requirement Clarity

- [ ] CHK016 - Is "session persists across app restarts" (FR-005) defined with a specific duration or expiry condition? [Clarity, Spec §FR-005]
- [ ] CHK017 - Is "role-based access" (FR-006) specified with an exhaustive list of what each role can and cannot do? [Clarity, Spec §FR-006]
- [ ] CHK018 - Is "amenities" in the Listing entity defined with an explicit data type (free text, enum list, multi-select)? [Ambiguity, Spec §FR-008]
- [ ] CHK019 - Is "type" in the Listing entity defined with an explicit set of allowed values? [Ambiguity, Spec §FR-008]
- [ ] CHK020 - Is "Gaborone areas" for location filtering defined with an explicit enumerated list of valid values? [Clarity, Spec §FR-016]
- [ ] CHK021 - Is "availability date" filtering defined — does it mean exact match, on-or-after, or within a range? [Ambiguity, Spec §FR-017]
- [ ] CHK022 - Is "simulated payment" defined with explicit steps and what mock data is accepted? [Clarity, Spec §FR-021]
- [ ] CHK023 - Is "unique reference number" (FR-022) defined with a specific format or generation strategy? [Clarity, Spec §FR-022]
- [ ] CHK024 - Is "Reserved" status defined as the only terminal state, or are other statuses (e.g., Cancelled, Expired) in scope? [Ambiguity, Spec §FR-024]
- [ ] CHK025 - Is "distance from campus" (FR-027) defined with a specific campus location coordinate? [Clarity, Spec §FR-027]
- [ ] CHK026 - Is "turn-by-turn directions" (FR-029) defined — does it mean in-app display or handoff to an external maps app? [Ambiguity, Spec §FR-029]
- [ ] CHK027 - Is "image compression" defined with specific target dimensions or quality percentage? [Ambiguity, Spec §FR-013]

---

## Requirement Consistency

- [ ] CHK028 - Are the required Listing fields consistent between FR-008 (creation), FR-012 (display), and the Key Entities section? [Consistency, Spec §FR-008, §FR-012]
- [ ] CHK029 - Are the required User fields consistent between FR-001 (registration) and the Key Entities section? [Consistency, Spec §FR-001]
- [ ] CHK030 - Does the "Provider Role" in the constitution align with the provider acceptance scenarios in User Story 6? [Consistency, Spec §US-6]
- [ ] CHK031 - Are the filter criteria in FR-015/016/017 consistent with the saved preferences fields in the UserPreferences entity? [Consistency, Spec §FR-015–018]
- [ ] CHK032 - Is the reservation receipt content in FR-023 consistent with the Reservation entity fields in Key Entities? [Consistency, Spec §FR-023]
- [ ] CHK033 - Are the success criteria in SC-001–SC-010 consistent with the acceptance scenarios in each User Story? [Consistency, Spec §Success Criteria]

---

## Acceptance Criteria Quality

- [ ] CHK034 - Are the acceptance scenarios for User Story 1 sufficient to cover the session persistence requirement (FR-005)? [Completeness, Spec §US-1]
- [ ] CHK035 - Is SC-001 ("under 1 minute") measurable given it depends on device speed — is a baseline device specified? [Measurability, Spec §SC-001]
- [ ] CHK036 - Is SC-004 ("100% of reservations") measurable with a defined test scope and concurrency condition? [Measurability, Spec §SC-004]
- [ ] CHK037 - Is SC-005 ("within 1 minute of matching listing creation") measurable given WorkManager scheduling constraints? [Measurability, Spec §SC-005]
- [ ] CHK038 - Is SC-009 ("within 5% margin of error") defined with a reference tool or method for verifying distance accuracy? [Measurability, Spec §SC-009]
- [ ] CHK039 - Are acceptance scenarios defined for the "update preferences" case in User Story 4, Scenario 4? [Completeness, Spec §US-4]

---

## Scenario Coverage

- [ ] CHK040 - Are requirements defined for the alternate flow where a student saves preferences but no matching listing ever exists? [Coverage, Spec §US-4]
- [ ] CHK041 - Are requirements defined for the concurrent reservation race condition (two students reserving simultaneously)? [Coverage, Spec §Edge Cases]
- [ ] CHK042 - Are requirements defined for the provider viewing their own listings vs. all listings? [Coverage, Gap]
- [ ] CHK043 - Are requirements defined for a student who has no saved preferences receiving notifications? [Coverage, Gap]
- [ ] CHK044 - Are requirements defined for what a student sees after their reserved listing is cancelled (if cancellation is in scope)? [Coverage, Gap]
- [ ] CHK045 - Are requirements defined for the app behavior when the device has no internet (for Maps/Directions)? [Coverage, Spec §FR-028]

---

## Edge Case Coverage

- [ ] CHK046 - Is the behavior defined when a student registers with an already-existing email? [Edge Case, Spec §Edge Cases]
- [ ] CHK047 - Is the behavior defined when image upload fails or the selected file is corrupted? [Edge Case, Spec §Edge Cases]
- [ ] CHK048 - Is the behavior defined when a provider submits a listing without uploading an image? [Edge Case, Spec §Edge Cases, FR-011]
- [ ] CHK049 - Is the behavior defined when all filters are applied and zero listings match? [Edge Case, Spec §Edge Cases]
- [ ] CHK050 - Is the behavior defined when a notification is tapped but the matching listing has since been reserved? [Edge Case, Gap]
- [ ] CHK051 - Is the behavior defined when the app is in the background or killed when a notification should fire? [Edge Case, Spec §Edge Cases]
- [ ] CHK052 - Is the behavior defined when a provider enters a past date as the availability date? [Edge Case, Spec §Validation Rules]

---

## Non-Functional Requirements

- [ ] CHK053 - Are password hashing requirements specific enough — is SHA-256 sufficient or is bcrypt/Argon2 required? [Clarity, Spec §FR-003]
- [ ] CHK054 - Are performance requirements defined for filter response time under maximum data load (50+ listings)? [Completeness, Spec §SC-003]
- [ ] CHK055 - Are accessibility requirements defined for any UI elements (e.g., content descriptions for images, contrast ratios)? [Gap]
- [ ] CHK056 - Are offline capability requirements explicitly scoped — which features work offline and which do not? [Gap]
- [ ] CHK057 - Are app size constraints (<100MB) traceable to specific features (e.g., image storage strategy)? [Traceability, Plan §Constraints]
- [ ] CHK058 - Are data retention requirements defined — is local data cleared on logout or persisted? [Gap]

---

## Dependencies & Assumptions

- [ ] CHK059 - Is the assumption that Google Maps SDK free tier is sufficient for the expected usage documented and validated? [Assumption, Plan §Risk Mitigation]
- [ ] CHK060 - Is the dependency on WorkManager for background notifications documented with its known limitations (e.g., Doze mode, battery optimization)? [Dependency, Plan §Phase 3]
- [ ] CHK061 - Is the assumption that all 50+ listings will have valid images documented as a data seeding requirement? [Assumption, Spec §FR-011]
- [ ] CHK062 - Is the campus location coordinate treated as a constant — is it documented and validated as the correct reference point? [Assumption, Spec §FR-027]

---

## Ambiguities & Conflicts

- [ ] CHK063 - Does the constitution list SHA-256 as acceptable while also listing bcrypt — is there a conflict on the hashing standard? [Conflict, Constitution §Security Requirements, Spec §FR-003]
- [ ] CHK064 - Is there a conflict between "offline-capable for browsing cached listings" (plan) and the absence of caching requirements in the spec? [Conflict, Plan §Constraints]
- [ ] CHK065 - Is the term "smart alerts" used in the constitution and spec consistently with the same scope as FR-019/FR-020? [Ambiguity, Spec §FR-019]
- [ ] CHK066 - Is a requirement ID and traceability scheme established to link spec FRs to tasks and acceptance scenarios? [Traceability]
