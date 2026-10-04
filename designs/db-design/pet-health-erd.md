# Pet Health Tracker — Database Design (ERD)

Converted from `designs/db-design/Pet Health - ERD.png`. Entities, keys, attributes and relationships are as drawn.
The original diagram has no data types, so the types below are **inferred** (Mermaid requires one per attribute).

```mermaid
erDiagram
    USER ||--o{ PET : owns
    PET  ||--o{ HEALTH_RECORD : has
    PET  ||--o{ MEDICATION : takes
    PET  ||--o{ APPOINTMENT : has

    USER {
        int    user_id PK
        string email
        string password
        string user_name "drawn as 'user name'"
    }

    PET {
        int    pet_id PK
        int    user_id FK
        string name
        string species
        string breed
        date   birth_date
        double weight
    }

    HEALTH_RECORD {
        int    record_id PK
        int    pet_id FK
        date   record_date
        string record_type
        string clinic_name
        string notes
    }

    MEDICATION {
        int    medication_id PK
        int    pet_id FK
        string medicine_name
        string dosage
        date   start_date
        date   end_date
    }

    APPOINTMENT {
        int    appointment_id PK
        int    pet_id FK
        date   appointment_date
        string clinic_name
        string visit_reason
        string notes
    }
```

Relationship notes: the PNG uses crow's-foot "one-or-many" (`|<`) on the child side. That's rendered here as zero-or-many (`o{`), because a new user has no pets yet and a new pet has no records. If you really mean "at least one", change `o{` to `|{`.
