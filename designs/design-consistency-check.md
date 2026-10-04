# Design Consistency Check — API vs DB vs Wireframes

Compared:

- **ERD**: `db-design/pet-health-erd.md`
- **OpenAPI**: `api-design/pet-health-api.yaml` (the only version; v1.0.0 was removed, see section 4)
- **API overview**: `api-design/pet-health-api-overview.md` (the original API and ERD PNGs were removed after being converted to Mermaid; they're in git history)

Severity: 🔴 will break or block implementation · 🟠 mismatch to resolve before coding · 🟡 convention or cleanup

---

## 1. ERD ↔ OpenAPI — ✅ resolved

Decision: **single-user MVP**. `USER` was removed from the ERD, and the API description says so. Multi-user/auth is a future feature.

| Was | Now |
|---|---|
| 🔴 `USER` and `pet.user_id` in the ERD, nothing in the API | `USER` dropped from the ERD |
| 🟠 ERD missing `gender`, `spayed_neutered`, `dose_frequency`, `appointment_time`, `status` | Added to the ERD |
| `ageYears`, `ageMonths`, `isActive` | Calculated, not stored (noted in both files) |
| 🟡 Weight had no unit | ERD `weight_lb`; API `weight` described as pounds |
| 🟡 `end_date` nullability | Nullable in the ERD (null = ongoing) |
| Enums and types missing from the ERD | ERD has data types; enum values listed, `CHECK` constraint noted |
| Delete behaviour undefined | `ON DELETE CASCADE` on all child tables |
| 🟡 `user name` with a space | Gone with `USER` |

Required fields now match on both sides: API `required:` lists ↔ ERD `NOT NULL`.

---

## 2. API PNGs ↔ OpenAPI — ✅ resolved

The API PNGs were removed. Their mismatches (`/pet` vs `/pets`, year/month/day date objects, keys with spaces, `"Check-up"` vs `CHECK_UP`) were fixed in `pet-health-api-overview.md`, which now matches the YAML. The 400/404 responses were added to the YAML.

Still to decide, against the UI wireframe (`ui-design/Pet Health Tracker.png`):

| # | Issue | Sev | Fix |
|---|---|---|---|
| 2.7 | Add Pet form may not have `gender` / `spayedNeutered` | 🟠 | Add both to the form |
| 2.10 | Pet profile shows medication **"Next dose"**, but `doseFrequency` is free text with no last-dose field, so the server can't compute it | 🟠 | Store `doseIntervalDays` + `lastDoseDate` and add a readOnly `nextDoseDate`, or drop "Next dose" from the UI |

---

## 3. Inside the OpenAPI file itself

| # | Issue | Sev | Fix |
|---|---|---|---|
| 3.1 | Path param is `{id}` on `/pets/{id}` but `{pet_id}` on the nested paths | 🟡 | Use one name everywhere (`{petId}` is the common OpenAPI style) |
| 3.2 | Sub-resources are singular and snake_case (`/health_record`, `/medication`, `/appointment`) while the root is plural (`/pets`) | 🟡 | REST convention: `/pets/{petId}/health-records`, `/medications`, `/appointments`. |
| 3.3 | `appointmentTime` is a plain string with no format or pattern | ✅ | Fixed: `HH:mm` pattern added |
| 3.4 | One schema is used for both request and response | ✅ | Works, because `readOnly` excludes `id`, age and `isActive` from requests |

---

## 4. Versions

There is only one API file now, `pet-health-api.yaml` (no version in the filename; `info.version` is `1.0.0`). The older v1.0.0 draft is in git history.

---

## Still open

1. Check the UI wireframe against 2.7 and 2.10.
2. Optional naming cleanup in the YAML (3.1–3.2).
