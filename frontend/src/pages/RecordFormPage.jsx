import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router'
import RecordForm from '../components/RecordForm.jsx'
import { RECORD_KINDS } from '../recordKinds.js'

// Add or edit a health record, medication or appointment.
// URLs: /pets/:id/:kind/new and /pets/:id/:kind/:recordId/edit,
// where :kind is health_record, medication or appointment.
export default function RecordFormPage() {
  const { id, kind, recordId } = useParams()
  const navigate = useNavigate()
  const recordKind = RECORD_KINDS[kind]
  const isEdit = recordId != null
  const [record, setRecord] = useState(null)
  const [error, setError] = useState(null)

  // When editing, load the record so the form starts with its current values.
  useEffect(() => {
    if (!recordKind || !isEdit) return
    recordKind.api
      .get(id, recordId)
      .then(setRecord)
      .catch((err) => setError(err.message))
  }, [recordKind, isEdit, id, recordId])

  if (!recordKind) return <p className="error">Page not found.</p>

  const backToProfile = () => navigate(`/pets/${id}`)

  async function handleSave(values) {
    if (isEdit) {
      await recordKind.api.update(id, recordId, values)
    } else {
      await recordKind.api.create(id, values)
    }
    backToProfile()
  }

  return (
    <section>
      <h1>
        {isEdit ? 'Edit' : 'Add'} {recordKind.title}
      </h1>
      {error && <p className="error">Couldn't load: {error}</p>}
      {isEdit && !record && !error && <p className="status">Loading…</p>}
      {(!isEdit || record) && (
        <RecordForm
          fields={recordKind.fields}
          initialRecord={record}
          onSave={handleSave}
          onCancel={backToProfile}
        />
      )}
    </section>
  )
}
