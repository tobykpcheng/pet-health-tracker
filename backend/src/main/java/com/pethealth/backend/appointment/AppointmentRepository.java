package com.pethealth.backend.appointment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /** Soonest first. */
    List<Appointment> findByPetIdOrderByAppointmentDateAscAppointmentTimeAsc(Long petId);

    Optional<Appointment> findByIdAndPetId(Long id, Long petId);
}
