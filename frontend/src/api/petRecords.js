// Calls to the health record, medication and appointment endpoints under /pets/{pet_id}.
import { request } from './client.js'

export function listHealthRecords(petId) {
  return request(`/pets/${petId}/health_record`)
}

export function listMedications(petId) {
  return request(`/pets/${petId}/medication`)
}

export function listAppointments(petId) {
  return request(`/pets/${petId}/appointment`)
}
