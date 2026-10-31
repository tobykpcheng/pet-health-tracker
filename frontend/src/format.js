// Small helpers for showing API values to people.

// "2022-11-12" -> "November 12, 2022". Built from the parts so the time zone can't shift the day.
export function formatDate(isoDate) {
  if (!isoDate) return null
  const [year, month, day] = isoDate.split('-').map(Number)
  return new Date(year, month - 1, day).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

// "3 yrs 9 mos", "9 mos", or null when the pet has no birth date.
export function formatAge(pet) {
  if (pet.ageYears == null) return null
  if (pet.ageYears === 0) return `${pet.ageMonths} mos`
  return `${pet.ageYears} yrs ${pet.ageMonths} mos`
}

const SPECIES_EMOJI = { dog: '🐶', cat: '🐱' }

export function speciesEmoji(species) {
  return SPECIES_EMOJI[species.toLowerCase()] ?? '🐾'
}

// Display names for the enum values in the API spec.
export const RECORD_TYPE_LABELS = {
  VACCINATION: 'Vaccination',
  CHECK_UP: 'Check-up',
  ILLNESS: 'Illness',
  SURGERY: 'Surgery',
  TEST_LAB: 'Test/Lab',
  OTHER: 'Other',
}

// The backend works out an appointment's status from its date and its cancelled flag.
export const APPOINTMENT_STATUS_LABELS = {
  UPCOMING: 'Upcoming',
  PAST: 'Past',
  CANCELLED: 'Cancelled',
}
