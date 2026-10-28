// Calls to the health record, medication and appointment endpoints under /pets/{pet_id}.
// All three work the same way, so one function builds the calls for each.
import { request } from './client.js'

function recordApi(segment) {
  return {
    list: (petId) => request(`/pets/${petId}/${segment}`),
    get: (petId, id) => request(`/pets/${petId}/${segment}/${id}`),
    create: (petId, record) => request(`/pets/${petId}/${segment}`, { method: 'POST', body: record }),
    update: (petId, id, record) => request(`/pets/${petId}/${segment}/${id}`, { method: 'PUT', body: record }),
    remove: (petId, id) => request(`/pets/${petId}/${segment}/${id}`, { method: 'DELETE' }),
  }
}

export const healthRecordApi = recordApi('health_record')
export const medicationApi = recordApi('medication')
export const appointmentApi = recordApi('appointment')
