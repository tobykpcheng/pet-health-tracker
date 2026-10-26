import { useState } from 'react'

const EMPTY_FORM = {
  name: '',
  species: '',
  breed: '',
  birthDate: '',
  weight: '',
  gender: '',
  spayedNeutered: '',
}

// The form keeps every field as a string, so turn a pet from the API into form values...
function toForm(pet) {
  return {
    name: pet.name,
    species: pet.species,
    breed: pet.breed ?? '',
    birthDate: pet.birthDate ?? '',
    weight: pet.weight == null ? '' : String(pet.weight),
    gender: pet.gender ?? '',
    spayedNeutered: pet.spayedNeutered == null ? '' : String(pet.spayedNeutered),
  }
}

// ...and back again. The API wants numbers, booleans and null for "not given".
function toPet(form) {
  return {
    name: form.name.trim(),
    species: form.species.trim(),
    breed: form.breed.trim() || null,
    birthDate: form.birthDate || null,
    weight: form.weight === '' ? null : Number(form.weight),
    gender: form.gender || null,
    spayedNeutered: form.spayedNeutered === '' ? null : form.spayedNeutered === 'true',
  }
}

// The pet form used by both Add Pet and Edit Pet.
// `initialPet` fills the fields when editing; `onSave` gets the pet to send to the API.
export default function PetForm({ initialPet, onSave, onCancel }) {
  const [form, setForm] = useState(initialPet ? toForm(initialPet) : EMPTY_FORM)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)

  // One change handler for every field: the input's name says which field to update.
  function handleChange(event) {
    const { name, value } = event.target
    setForm({ ...form, [name]: value })
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSaving(true)
    setError(null)
    try {
      await onSave(toPet(form))
    } catch (err) {
      setError(err.message)
      setSaving(false)
    }
  }

  const today = new Date().toISOString().slice(0, 10)

  return (
    <form className="card pet-form" onSubmit={handleSubmit}>
      <label>
        Name *
        <input name="name" value={form.name} onChange={handleChange} required maxLength={100} />
      </label>
      <label>
        Species *
        <input
          name="species"
          value={form.species}
          onChange={handleChange}
          required
          maxLength={50}
          placeholder="Dog, Cat…"
        />
      </label>
      <label>
        Breed
        <input name="breed" value={form.breed} onChange={handleChange} maxLength={100} />
      </label>
      <label>
        Birth date
        <input type="date" name="birthDate" value={form.birthDate} onChange={handleChange} max={today} />
      </label>
      <label>
        Weight (lb)
        <input type="number" name="weight" value={form.weight} onChange={handleChange} min="0" step="0.01" />
      </label>
      <label>
        Gender
        <select name="gender" value={form.gender} onChange={handleChange}>
          <option value="">Not specified</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
      </label>
      <label>
        Spayed / neutered
        <select name="spayedNeutered" value={form.spayedNeutered} onChange={handleChange}>
          <option value="">Not specified</option>
          <option value="true">Yes</option>
          <option value="false">No</option>
        </select>
      </label>

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
