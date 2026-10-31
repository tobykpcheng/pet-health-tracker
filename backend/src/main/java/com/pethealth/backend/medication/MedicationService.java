package com.pethealth.backend.medication;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pethealth.backend.common.NotFoundException;
import com.pethealth.backend.pet.PetService;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final PetService petService;

    public MedicationService(MedicationRepository medicationRepository, PetService petService) {
        this.medicationRepository = medicationRepository;
        this.petService = petService;
    }

    public List<Medication> findAll(Long petId) {
        petService.findById(petId); // 404 if the pet doesn't exist
        return medicationRepository.findByPetIdOrderByStartDateDesc(petId);
    }

    /** Only finds the medication if it belongs to this pet. */
    public Medication findById(Long petId, Long medicationId) {
        return medicationRepository.findByIdAndPetId(medicationId, petId)
                .orElseThrow(() -> new NotFoundException(
                        "Medication " + medicationId + " not found for pet " + petId));
    }

    public Medication create(Long petId, Medication medication) {
        medication.setPet(petService.findById(petId));
        return medicationRepository.save(medication);
    }

    @Transactional
    public Medication update(Long petId, Long medicationId, Medication changes) {
        Medication medication = findById(petId, medicationId);
        medication.setMedicineName(changes.getMedicineName());
        medication.setDosage(changes.getDosage());
        medication.setDirectionsForUse(changes.getDirectionsForUse());
        medication.setStartDate(changes.getStartDate());
        medication.setEndDate(changes.getEndDate());
        return medication;
    }

    public void delete(Long petId, Long medicationId) {
        medicationRepository.delete(findById(petId, medicationId));
    }
}
