# Pet Health Tracker — Database Design (ERD)

Matches `designs/api-design/pet-health-api.yaml`. Database columns use `snake_case` and API fields use `camelCase` (JPA maps one to the other).

**Single-user MVP:** there is no `USER` table. The app has one user, so pets aren't linked to an owner. Multi-user support (a `USER` table plus `pet.user_id`) is a future feature.

```mermaid
erDiagram
    PET  ||--o{ HEALTH_RECORD : has
    PET  ||--o{ MEDICATION : takes
    PET  ||--o{ APPOINTMENT : has

    PET {
        bigint  pet_id PK
        varchar name "NOT NULL"
        varchar species "NOT NULL"
        varchar breed
        date    birth_date
        decimal weight_lb
        varchar gender "MALE | FEMALE"
        boolean spayed_neutered
    }

    HEALTH_RECORD {
        bigint  record_id PK
        bigint  pet_id FK "NOT NULL, ON DELETE CASCADE"
        date    record_date "NOT NULL"
        varchar record_type "NOT NULL: VACCINATION | CHECK_UP | ILLNESS | SURGERY | TEST_LAB | OTHER"
        varchar clinic_name
        text    notes
    }

    MEDICATION {
        bigint  medication_id PK
        bigint  pet_id FK "NOT NULL, ON DELETE CASCADE"
        varchar medicine_name "NOT NULL"
        varchar dosage
        varchar directions_for_use
        date    start_date "NOT NULL"
        date    end_date "nullable: null = ongoing"
    }

    APPOINTMENT {
        bigint  appointment_id PK
        bigint  pet_id FK "NOT NULL, ON DELETE CASCADE"
        date    appointment_date "NOT NULL"
        time    appointment_time "NOT NULL"
        varchar clinic_name
        varchar visit_reason
        text    notes
        bit     cancelled "NOT NULL, default 0"
    }
```

## Notes

- **Column names that don't follow the camelCase → snake_case rule** need an explicit `@Column(name = ...)`: API `id` → `pet_id` / `record_id` / `medication_id` / `appointment_id`, and API `weight` → `weight_lb`.
- **Enums** (`gender`, `record_type`) are stored as strings with the same values as the API. Add a `CHECK` constraint for each; with JPA, use `@Enumerated(EnumType.STRING)`.
- **Deleting a pet** deletes its health records, medications and appointments (`ON DELETE CASCADE`, or JPA `cascade = CascadeType.REMOVE, orphanRemoval = true`).
- **Relationships** are zero-or-many (`o{`): a new pet has no records yet.
