// Shared helper for calling the backend described in designs/api-design/pet-health-api.yaml.
// The backend runs at http://localhost:8080 (see "Local setup" in the README).
const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

// Sends a request and returns the parsed JSON (or null for 204 No Content).
// Throws an Error with the backend's message when the response isn't 2xx.
export async function request(path, { method = 'GET', body } = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    method,
    headers: body ? { 'Content-Type': 'application/json' } : undefined,
    body: body ? JSON.stringify(body) : undefined,
  })

  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try {
      const problem = await response.json()
      if (problem.detail) message = problem.detail
    } catch {
      // The body wasn't JSON; keep the generic message.
    }
    throw new Error(message)
  }

  return response.status === 204 ? null : response.json()
}
