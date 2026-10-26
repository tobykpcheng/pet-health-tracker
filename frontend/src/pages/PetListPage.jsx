import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { listPets } from '../api/pets.js'

const SPECIES_EMOJI = { dog: '🐶', cat: '🐱' }

// "3 yrs 9 mos", "9 mos", or null when the pet has no birth date.
function formatAge(pet) {
  if (pet.ageYears == null) return null
  if (pet.ageYears === 0) return `${pet.ageMonths} mos`
  return `${pet.ageYears} yrs ${pet.ageMonths} mos`
}

function PetCard({ pet }) {
  const age = formatAge(pet)
  return (
    <article className="card pet-card">
      <h2>
        <span aria-hidden="true">{SPECIES_EMOJI[pet.species.toLowerCase()] ?? '🐾'}</span> {pet.name}
      </h2>
      <p>
        {pet.species}
        {pet.breed && ` · ${pet.breed}`}
      </p>
      {age && <p>Age: {age}</p>}
      {pet.weight != null && <p>Weight: {pet.weight} lb</p>}
    </article>
  )
}

export default function PetListPage() {
  const [pets, setPets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Load the pets once, when the page first appears.
  useEffect(() => {
    listPets()
      .then(setPets)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  return (
    <section>
      <h1>My Pets</h1>
      {loading && <p className="status">Loading pets…</p>}
      {error && <p className="error">Couldn't load pets: {error}. Is the backend running?</p>}
      {!loading && !error && pets.length === 0 && (
        <p className="status">No pets yet. Add your first one!</p>
      )}
      <div className="pet-list">
        {pets.map((pet) => (
          <PetCard key={pet.id} pet={pet} />
        ))}
        <Link to="/pets/new" className="card add-pet-card">
          <span className="plus">+</span>
          Add a pet
        </Link>
      </div>
    </section>
  )
}
