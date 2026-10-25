package com.pethealth.backend.healthrecord;

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

/** The /pets/{pet_id}/health_record endpoints from designs/api-design/pet-health-api.yaml. */
@RestController
@RequestMapping("/pets/{petId}/health_record")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @GetMapping
    public List<HealthRecord> list(@PathVariable Long petId) {
        return healthRecordService.findAll(petId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HealthRecord create(@PathVariable Long petId, @Valid @RequestBody HealthRecord record) {
        return healthRecordService.create(petId, record);
    }

    @GetMapping("/{recordId}")
    public HealthRecord get(@PathVariable Long petId, @PathVariable Long recordId) {
        return healthRecordService.findById(petId, recordId);
    }

    @PutMapping("/{recordId}")
    public HealthRecord update(@PathVariable Long petId, @PathVariable Long recordId,
            @Valid @RequestBody HealthRecord record) {
        return healthRecordService.update(petId, recordId, record);
    }

    @DeleteMapping("/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long petId, @PathVariable Long recordId) {
        healthRecordService.delete(petId, recordId);
    }
}
