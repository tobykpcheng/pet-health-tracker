package com.pethealth.backend.appointment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pethealth.backend.common.NotFoundException;
import com.pethealth.backend.pet.PetService;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetService petService;

    public AppointmentService(AppointmentRepository appointmentRepository, PetService petService) {
        this.appointmentRepository = appointmentRepository;
        this.petService = petService;
    }

    public List<Appointment> findAll(Long petId) {
        petService.findById(petId); // 404 if the pet doesn't exist
        return appointmentRepository.findByPetIdOrderByAppointmentDateAscAppointmentTimeAsc(petId);
    }

    /** Only finds the appointment if it belongs to this pet. */
    public Appointment findById(Long petId, Long appointmentId) {
        return appointmentRepository.findByIdAndPetId(appointmentId, petId)
                .orElseThrow(() -> new NotFoundException(
                        "Appointment " + appointmentId + " not found for pet " + petId));
    }

    public Appointment create(Long petId, Appointment appointment) {
        appointment.setPet(petService.findById(petId));
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment update(Long petId, Long appointmentId, Appointment changes) {
        Appointment appointment = findById(petId, appointmentId);
        appointment.setAppointmentDate(changes.getAppointmentDate());
        appointment.setAppointmentTime(changes.getAppointmentTime());
        appointment.setClinicName(changes.getClinicName());
        appointment.setVisitReason(changes.getVisitReason());
        appointment.setNotes(changes.getNotes());
        appointment.setCancelled(changes.isCancelled());
        return appointment;
    }

    public void delete(Long petId, Long appointmentId) {
        appointmentRepository.delete(findById(petId, appointmentId));
    }
}
