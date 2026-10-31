// Describes the three kinds of pet records: their API calls and their form fields.
// RecordFormPage uses this to build the Add/Edit form for any kind, so adding a field
// here is all it takes to show it in the form.
import { appointmentApi, healthRecordApi, medicationApi } from './api/petRecords.js'
import { APPOINTMENT_STATUS_LABELS, RECORD_TYPE_LABELS } from './format.js'

// { VACCINATION: 'Vaccination', ... } -> [{ value: 'VACCINATION', label: 'Vaccination' }, ...]
function toOptions(labels) {
  return Object.entries(labels).map(([value, label]) => ({ value, label }))
}

// Field types: 'date', 'time', 'text', 'textarea' and 'select'.
// The keys (health_record, medication, appointment) are also the URL segments.
export const RECORD_KINDS = {
  health_record: {
    title: 'Health Record',
    api: healthRecordApi,
    fields: [
      { name: 'recordDate', label: 'Date', type: 'date', required: true, notFuture: true },
      { name: 'recordType', label: 'Type of record', type: 'select', required: true, options: toOptions(RECORD_TYPE_LABELS) },
      { name: 'clinicName', label: 'Vet / clinic', type: 'text', maxLength: 100 },
      { name: 'notes', label: 'Notes', type: 'textarea' },
    ],
  },
  medication: {
    title: 'Medication',
    api: medicationApi,
    fields: [
      { name: 'medicineName', label: 'Medicine', type: 'text', required: true, maxLength: 100 },
      { name: 'dosage', label: 'Dosage', type: 'text', maxLength: 50, placeholder: 'e.g. 30 mg' },
      { name: 'directionsForUse', label: 'Directions for use', type: 'text', maxLength: 100, placeholder: 'e.g. 1 chewable tablet every 30 days, with food' },
      { name: 'startDate', label: 'Start date', type: 'date', required: true },
      { name: 'endDate', label: 'End date (leave empty if ongoing)', type: 'date', notBefore: 'startDate' },
    ],
  },
  appointment: {
    title: 'Appointment',
    api: appointmentApi,
    fields: [
      { name: 'appointmentDate', label: 'Date', type: 'date', required: true },
      { name: 'appointmentTime', label: 'Time', type: 'time', required: true },
      { name: 'clinicName', label: 'Vet / clinic', type: 'text', maxLength: 100 },
      { name: 'visitReason', label: 'Reason for visit', type: 'text', maxLength: 255 },
      { name: 'status', label: 'Status', type: 'select', required: true, options: toOptions(APPOINTMENT_STATUS_LABELS), defaultValue: 'UPCOMING' },
      { name: 'notes', label: 'Notes', type: 'textarea' },
    ],
  },
}
