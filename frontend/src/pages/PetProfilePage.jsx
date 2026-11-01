import { useCallback, useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router'
import { deletePet, getPet } from '../api/pets.js'
import { appointmentApi, healthRecordApi, medicationApi } from '../api/petRecords.js'
import {
  APPOINTMENT_STATUS_LABELS,
  RECORD_TYPE_LABELS,
  formatAge,
  formatDate,
  speciesEmoji,
} from '../format.js'

function PetDetails({ pet }) {
  const age = formatAge(pet)
  const genderLabel = { MALE: 'Male', FEMALE: 'Female' }[pet.gender]
  return (
    <dl className="pet-details">
      <dt>Species</dt>
      <dd>{pet.species}</dd>
      {pet.breed && (
        <>
          <dt>Breed</dt>
          <dd>{pet.breed}</dd>
        </>
      )}
      {pet.birthDate && (
        <>
          <dt>Birth date</dt>
          <dd>
            {formatDate(pet.birthDate)} ({age})
          </dd>
        </>
      )}
      {pet.weight != null && (
        <>
          <dt>Weight</dt>
          <dd>{pet.weight} lb</dd>
        </>
      )}
      {genderLabel && (
        <>
          <dt>Gender</dt>
          <dd>{genderLabel}</dd>
        </>
      )}
      {pet.spayedNeutered != null && (
        <>
          <dt>Spayed / neutered</dt>
          <dd>{pet.spayedNeutered ? 'Yes' : 'No'}</dd>
        </>
      )}
    </dl>
  )
}

// One coloured section (health records, medication or appointments) with its list of items,
// an Edit and Delete button on each item, and an Add button at the bottom.
// `kind` is the URL segment for this kind of record, e.g. "health_record".
function RecordSection({ petId, kind, title, addLabel, className, items, emptyText, renderItem, onDelete }) {
  return (
    <section className={`record-section ${className}`}>
      <h2>{title}</h2>
      {items.length === 0 ? (
        <p className="status">{emptyText}</p>
      ) : (
        <ul>
          {items.map((item) => (
            <li key={item.id}>
              <div className="record-item">{renderItem(item)}</div>
              <div className="record-actions">
                <Link to={`/pets/${petId}/${kind}/${item.id}/edit`}>Edit</Link>
                <button type="button" className="link-button" onClick={() => onDelete(item)}>
                  Delete
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
      <Link to={`/pets/${petId}/${kind}/new`} className="button add-record">
        + {addLabel}
      </Link>
    </section>
  )
}

export default function PetProfilePage() {
  const { id } = useParams() // the :id part of the URL /pets/:id
  const navigate = useNavigate()
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)

  // Load the pet and its three lists at the same time.
  // useCallback keeps the same function between renders, so the effect below runs only when id changes.
  const loadAll = useCallback(() => {
    return Promise.all([getPet(id), healthRecordApi.list(id), medicationApi.list(id), appointmentApi.list(id)])
      .then(([pet, records, medications, appointments]) => setData({ pet, records, medications, appointments }))
      .catch((err) => setError(err.message))
  }, [id])

  useEffect(() => {
    loadAll()
  }, [loadAll])

  // Returns the delete handler for one kind of record: confirm, delete, then reload the lists.
  function deleteRecord(api, describe) {
    return async (item) => {
      if (!window.confirm(`Delete ${describe(item)}?`)) return
      try {
        await api.remove(id, item.id)
        await loadAll()
      } catch (err) {
        setError(err.message)
      }
    }
  }

  async function handleDelete() {
    const confirmed = window.confirm(
      `Delete ${data.pet.name}? This also deletes all of their health records, medications and appointments.`,
    )
    if (!confirmed) return
    try {
      await deletePet(id)
      navigate('/')
    } catch (err) {
      setError(err.message)
    }
  }

  if (error) return <p className="error">Couldn't load pet: {error}</p>
  if (!data) return <p className="status">Loading…</p>

  const { pet, medications } = data
  // Newest health records first; appointments in date order.
  const records = [...data.records].sort((a, b) => b.recordDate.localeCompare(a.recordDate))
  const appointments = [...data.appointments].sort((a, b) =>
    (a.appointmentDate + a.appointmentTime).localeCompare(b.appointmentDate + b.appointmentTime),
  )

  return (
    <section>
      <Link to="/" className="back-link">
        ← My Pets
      </Link>
      <div className="card profile">
        <h1>
          <span aria-hidden="true">{speciesEmoji(pet.species)}</span> {pet.name}
        </h1>
        <PetDetails pet={pet} />
        <div className="form-buttons">
          <Link to={`/pets/${id}/edit`} className="button edit">
            Edit
          </Link>
          <button type="button" className="button delete" onClick={handleDelete}>
            Delete
          </button>
        </div>

        <RecordSection
          petId={id}
          kind="health_record"
          title="Health records"
          addLabel="Add health record"
          className="health"
          items={records}
          emptyText="No health records yet."
          onDelete={deleteRecord(
            healthRecordApi,
            (r) => `the ${RECORD_TYPE_LABELS[r.recordType]} record from ${formatDate(r.recordDate)}`,
          )}
          renderItem={(record) => (
            <>
              <strong>{formatDate(record.recordDate)}</strong> · {RECORD_TYPE_LABELS[record.recordType]}
              {record.clinicName && <div>Vet / clinic: {record.clinicName}</div>}
              {record.notes && <div>Notes: {record.notes}</div>}
            </>
          )}
        />

        <RecordSection
          petId={id}
          kind="medication"
          title="Medication"
          addLabel="Add medication"
          className="medication"
          items={medications}
          emptyText="No medications yet."
          onDelete={deleteRecord(medicationApi, (m) => m.medicineName)}
          renderItem={(med) => (
            <>
              <strong>{med.medicineName}</strong>
              {med.dosage && ` · ${med.dosage}`}
              {med.directionsForUse && ` · ${med.directionsForUse}`}
              {!med.isActive && <span className="tag">Ended</span>}
              <div>
                From {formatDate(med.startDate)}
                {med.endDate && ` to ${formatDate(med.endDate)}`}
              </div>
            </>
          )}
        />

        <RecordSection
          petId={id}
          kind="appointment"
          title="Appointments"
          addLabel="Add appointment"
          className="appointments"
          items={appointments}
          emptyText="No appointments yet."
          onDelete={deleteRecord(
            appointmentApi,
            (a) => `the appointment on ${formatDate(a.appointmentDate)} at ${a.appointmentTime}`,
          )}
          renderItem={(appt) => (
            <>
              <strong>
                {formatDate(appt.appointmentDate)}, {appt.appointmentTime}
              </strong>
              {appt.clinicName && ` · ${appt.clinicName}`}
              <span className={`tag tag-${appt.status.toLowerCase()}`}>
                {APPOINTMENT_STATUS_LABELS[appt.status]}
              </span>
              {appt.visitReason && <div>Reason: {appt.visitReason}</div>}
              {appt.notes && <div>Notes: {appt.notes}</div>}
            </>
          )}
        />
      </div>
    </section>
  )
}
