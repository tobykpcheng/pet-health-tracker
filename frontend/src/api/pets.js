// Calls to the /pets endpoints in designs/api-design/pet-health-api.yaml.
// The backend runs at http://localhost:8080 (see "Local setup" in the README).
const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

// Throws an Error with the backend's message when the response isn't 2xx.
async function checkResponse(response) {
  if (response.ok) return response
  let message = `Request failed (${response.status})`
  try {
    const problem = await response.json()
    if (problem.detail) message = problem.detail
  } catch {
    // The body wasn't JSON; keep the generic message.
  }
  throw new Error(message)
}

export async function listPets() {
  const response = await fetch(`${API_URL}/pets`)
  await checkResponse(response)
  return response.json()
}

export async function createPet(pet) {
  const response = await fetch(`${API_URL}/pets`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(pet),
  })
  await checkResponse(response)
  return response.json()
}
