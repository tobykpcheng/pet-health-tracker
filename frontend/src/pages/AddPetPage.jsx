import { useNavigate } from 'react-router'
import { createPet } from '../api/pets.js'
import PetForm from '../components/PetForm.jsx'

export default function AddPetPage() {
  const navigate = useNavigate()

  async function handleSave(pet) {
    await createPet(pet)
    navigate('/')
  }

  return (
    <section>
      <h1>Add Pet</h1>
      <PetForm onSave={handleSave} onCancel={() => navigate('/')} />
    </section>
  )
}
