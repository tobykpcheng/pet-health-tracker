// Calls to the /pets endpoints.
import { request } from './client.js'

export function listPets() {
  return request('/pets')
}

export function getPet(id) {
  return request(`/pets/${id}`)
}

export function createPet(pet) {
  return request('/pets', { method: 'POST', body: pet })
}

export function updatePet(id, pet) {
  return request(`/pets/${id}`, { method: 'PUT', body: pet })
}

export function deletePet(id) {
  return request(`/pets/${id}`, { method: 'DELETE' })
}
