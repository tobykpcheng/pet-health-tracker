package com.pethealth.backend.medication;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/** The /pets/{pet_id}/medication endpoints from designs/api-design/pet-health-api.yaml. */
@RestController
@RequestMapping("/pets/{petId}/medication")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public List<Medication> list(@PathVariable Long petId) {
        return medicationService.findAll(petId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medication create(@PathVariable Long petId, @Valid @RequestBody Medication medication) {
        return medicationService.create(petId, medication);
    }

    @GetMapping("/{medicationId}")
    public Medication get(@PathVariable Long petId, @PathVariable Long medicationId) {
        return medicationService.findById(petId, medicationId);
    }

    @PutMapping("/{medicationId}")
    public Medication update(@PathVariable Long petId, @PathVariable Long medicationId,
            @Valid @RequestBody Medication medication) {
        return medicationService.update(petId, medicationId, medication);
    }

    @DeleteMapping("/{medicationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long petId, @PathVariable Long medicationId) {
        medicationService.delete(petId, medicationId);
    }
}
