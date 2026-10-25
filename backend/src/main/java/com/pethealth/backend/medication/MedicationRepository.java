package com.pethealth.backend.medication;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /** Most recently started first. */
    List<Medication> findByPetIdOrderByStartDateDesc(Long petId);

    Optional<Medication> findByIdAndPetId(Long id, Long petId);
}
