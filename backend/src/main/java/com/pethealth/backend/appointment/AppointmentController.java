package com.pethealth.backend.appointment;

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

/** The /pets/{pet_id}/appointment endpoints from designs/api-design/pet-health-api.yaml. */
@RestController
@RequestMapping("/pets/{petId}/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<Appointment> list(@PathVariable Long petId) {
        return appointmentService.findAll(petId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@PathVariable Long petId, @Valid @RequestBody Appointment appointment) {
        return appointmentService.create(petId, appointment);
    }

    @GetMapping("/{appointmentId}")
    public Appointment get(@PathVariable Long petId, @PathVariable Long appointmentId) {
        return appointmentService.findById(petId, appointmentId);
    }

    @PutMapping("/{appointmentId}")
    public Appointment update(@PathVariable Long petId, @PathVariable Long appointmentId,
            @Valid @RequestBody Appointment appointment) {
        return appointmentService.update(petId, appointmentId, appointment);
    }

    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long petId, @PathVariable Long appointmentId) {
        appointmentService.delete(petId, appointmentId);
    }
}
