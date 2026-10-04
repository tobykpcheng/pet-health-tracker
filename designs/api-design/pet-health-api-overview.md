# Pet Health Tracker — API Design Overview

The source of truth for the API is `pet-health-api_V1.0.1.yaml` (OpenAPI 3.0.3), which is already YAML.
This file turns the **wireframe → endpoint PNGs** into Mermaid and summarises the YAML so you can compare them.

## 1. UI flow → endpoints (from the REST API PNGs, as drawn)

Paths and payloads are copied exactly as they appear in the images, typos included. They're compared against the YAML in the consistency report.

```mermaid
flowchart LR
    login([User logs in]) -->|"GET /pets"| dash[Dashboard<br/>My Pets]
    dash -->|"GET /pet/{pet_id}"| profile[Pet Profile]
    dash -->|"Add a pet → POST /pets"| addPet[Add Pet form]

    profile -->|"GET /pets/{pet_id}/health_record"| hrList[Health Records list]
    profile -->|"GET /pets/{pet_id}/medication"| medList[Medications list]
    profile -->|"GET /pet/{pet_id}/appointment"| apptList[Appointments list]

    hrList -->|"+ Add → POST /pets/{pet_id}/health_record"| addHr[Add Health Record form]
    hrList -->|Edit| editHr[Edit Health Record form]
    editHr -->|"Save → PUT /pets/{pet_id}/health_record/{record_id}"| hrList
    hrList -->|"Delete → DELETE /pets/{pet_id}/health_record/{record_id}"| hrList
```

### Request bodies shown in the PNGs

```yaml
# POST /pets   (REST API_add a pet.png)
name: Maru
species: Dog
breed: Maltipoo
"birth date": { year: 2022, month: 11, day: 12 }
weight: 10

# POST /pets/{pet_id}/health_record   (REST API_add health record.png)
"record date": { year: 2026, month: 8, day: 15 }
"record type": Check-up
"clinic name": A Niu
Notes: Lorem ipsum dolor sit amet

# PUT /pets/{pet_id}/health_record/{record_id}   (REST API_edit-delete health record.png)
"record date": { year: 2025, month: 9, day: 28 }
"record type": Other
"clinic name": Burquitlam Animal Hospital
Notes: Lorem ipsum dolor sit amet
```

### Status codes (CRUD endpoints status code.png)

| Situation | Code |
|---|---|
| GET succeeds | 200 |
| POST succeeds | 201 |
| PUT succeeds | 200 |
| DELETE succeeds | 204 |
| Bad / invalid data | 400 |
| GET/PUT/DELETE on a missing ID | 404 |

## 2. Endpoint map (from pet-health-api_V1.0.1.yaml)

```mermaid
flowchart TB
    root["/pets<br/>GET 200 · POST 201"]
    pet["/pets/{id}<br/>GET 200/404 · PUT 200/404 · DELETE 204/404"]
    hr["/pets/{pet_id}/health_record<br/>GET 200 · POST 201"]
    hrId["/pets/{pet_id}/health_record/{record_id}<br/>GET · PUT · DELETE"]
    med["/pets/{pet_id}/medication<br/>GET 200 · POST 201"]
    medId["/pets/{pet_id}/medication/{medication_id}<br/>GET · PUT · DELETE"]
    ap["/pets/{pet_id}/appointment<br/>GET 200 · POST 201"]
    apId["/pets/{pet_id}/appointment/{appointment_id}<br/>GET · PUT · DELETE"]

    root --> pet
    pet --> hr --> hrId
    pet --> med --> medId
    pet --> ap --> apId
```

## 3. Schemas (from pet-health-api_V1.0.1.yaml)

`«ro»` = `readOnly: true` (computed or generated on the server).

```mermaid
classDiagram
    class Pet {
        integer id «ro»
        string name
        string species
        string breed
        date birthDate
        double weight
        enum gender [MALE, FEMALE]
        boolean spayedNeutered
        integer ageYears «ro»
        integer ageMonths «ro»
    }
    class HealthRecord {
        integer id «ro»
        date recordDate
        enum recordType [VACCINATION, CHECK_UP, ILLNESS, SURGERY, TEST_LAB, OTHER]
        string clinicName
        string notes
    }
    class Medication {
        integer id «ro»
        string medicineName
        string dosage
        date startDate
        date endDate ‹nullable›
        boolean isActive «ro»
        string doseFrequency
    }
    class Appointment {
        integer id «ro»
        date appointmentDate
        string appointmentTime
        string clinicName
        string visitReason
        string notes
        enum status [UPCOMING, COMPLETED, CANCELLED]
    }
    Pet "1" --> "*" HealthRecord
    Pet "1" --> "*" Medication
    Pet "1" --> "*" Appointment
```
