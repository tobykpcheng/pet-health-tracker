package com.pethealth.backend.healthrecord;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    /** Newest first. */
    List<HealthRecord> findByPetIdOrderByRecordDateDesc(Long petId);

    Optional<HealthRecord> findByIdAndPetId(Long id, Long petId);
}
