package com.pethealth.backend.healthrecord;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pethealth.backend.common.NotFoundException;
import com.pethealth.backend.pet.PetService;

@Service
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final PetService petService;

    public HealthRecordService(HealthRecordRepository healthRecordRepository, PetService petService) {
        this.healthRecordRepository = healthRecordRepository;
        this.petService = petService;
    }

    public List<HealthRecord> findAll(Long petId) {
        petService.findById(petId); // 404 if the pet doesn't exist
        return healthRecordRepository.findByPetIdOrderByRecordDateDesc(petId);
    }

    /** Only finds the record if it belongs to this pet, so /pets/1/health_record/5 can't return pet 2's record. */
    public HealthRecord findById(Long petId, Long recordId) {
        return healthRecordRepository.findByIdAndPetId(recordId, petId)
                .orElseThrow(() -> new NotFoundException(
                        "Health record " + recordId + " not found for pet " + petId));
    }

    public HealthRecord create(Long petId, HealthRecord record) {
        record.setPet(petService.findById(petId));
        return healthRecordRepository.save(record);
    }

    @Transactional
    public HealthRecord update(Long petId, Long recordId, HealthRecord changes) {
        HealthRecord record = findById(petId, recordId);
        record.setRecordDate(changes.getRecordDate());
        record.setRecordType(changes.getRecordType());
        record.setClinicName(changes.getClinicName());
        record.setNotes(changes.getNotes());
        return record;
    }

    public void delete(Long petId, Long recordId) {
        healthRecordRepository.delete(findById(petId, recordId));
    }
}
