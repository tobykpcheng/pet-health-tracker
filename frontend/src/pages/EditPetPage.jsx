import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router'
import { getPet, updatePet } from '../api/pets.js'
import PetForm from '../components/PetForm.jsx'

export default function EditPetPage() {
  const { id } = useParams() // the :id part of the URL /pets/:id/edit
  const navigate = useNavigate()
  const [pet, setPet] = useState(null)
  const [error, setError] = useState(null)

  // Load the pet so the form can start with its current values.
  useEffect(() => {
    getPet(id)
      .then(setPet)
      .catch((err) => setError(err.message))
  }, [id])

  async function handleSave(changes) {
    await updatePet(id, changes)
    navigate(`/pets/${id}`)
  }

  return (
    <section>
      <h1>Edit Pet</h1>
      {error && <p className="error">Couldn't load pet: {error}</p>}
      {!pet && !error && <p className="status">Loading…</p>}
      {pet && <PetForm initialPet={pet} onSave={handleSave} onCancel={() => navigate(`/pets/${id}`)} />}
    </section>
  )
}
