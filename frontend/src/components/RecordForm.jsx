import { useState } from 'react'

// The form keeps every field as a string ('' when empty)...
function toForm(fields, record) {
  return Object.fromEntries(fields.map((f) => [f.name, record?.[f.name] ?? f.defaultValue ?? '']))
}

// ...and sends null for empty fields, with text trimmed.
function toRecord(fields, form) {
  return Object.fromEntries(fields.map((f) => [f.name, form[f.name].trim() || null]))
}

function FieldInput({ field, form, onChange }) {
  const common = {
    name: field.name,
    value: form[field.name],
    onChange,
    required: field.required,
  }

  if (field.type === 'select') {
    return (
      <select {...common}>
        {!field.required && <option value="">Not specified</option>}
        {field.required && !field.defaultValue && <option value="">Choose…</option>}
        {field.options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    )
  }

  if (field.type === 'textarea') {
    return <textarea {...common} rows={3} />
  }

  const today = new Date().toISOString().slice(0, 10)
  return (
    <input
      {...common}
      type={field.type}
      maxLength={field.maxLength}
      placeholder={field.placeholder}
      max={field.notFuture ? today : undefined}
      min={field.notBefore ? form[field.notBefore] || undefined : undefined}
    />
  )
}

// A form for any record kind. `fields` comes from RECORD_KINDS in recordKinds.js.
export default function RecordForm({ fields, initialRecord, onSave, onCancel }) {
  const [form, setForm] = useState(() => toForm(fields, initialRecord))
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)

  function handleChange(event) {
    const { name, value } = event.target
    setForm({ ...form, [name]: value })
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSaving(true)
    setError(null)
    try {
      await onSave(toRecord(fields, form))
    } catch (err) {
      setError(err.message)
      setSaving(false)
    }
  }

  return (
    <form className="card pet-form" onSubmit={handleSubmit}>
      {fields.map((field) => (
        <label key={field.name}>
          {field.label}
          {field.required && ' *'}
          <FieldInput field={field} form={form} onChange={handleChange} />
        </label>
      ))}

      {error && <p className="error">Couldn't save: {error}</p>}

      <div className="form-buttons">
        <button type="button" className="button cancel" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="button save" disabled={saving}>
          {saving ? 'Saving…' : 'Save'}
        </button>
      </div>
    </form>
  )
}
