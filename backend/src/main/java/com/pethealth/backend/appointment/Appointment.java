package com.pethealth.backend.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pethealth.backend.pet.Pet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** A vet appointment. Maps to the APPOINTMENT table in designs/db-design/pet-health-erd.md. */
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /** The pet this appointment is for. Comes from the URL, not the JSON body. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Pet pet;

    @NotNull
    @Column(nullable = false)
    private LocalDate appointmentDate;

    /** 24-hour time, sent and received as "HH:mm" (e.g. "13:30"). */
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    @Column(nullable = false)
    private LocalTime appointmentTime;

    @Size(max = 100)
    @Column(length = 100)
    private String clinicName;

    @Size(max = 255)
    private String visitReason;

    @Lob
    private String notes;

    /** True when the appointment was cancelled. Optional in the JSON body; defaults to false. */
    @Column(nullable = false)
    private boolean cancelled;

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getVisitReason() {
        return visitReason;
    }

    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * CANCELLED if cancelled, otherwise UPCOMING or PAST from the date and time.
     * Calculated, not stored, so it can never disagree with the date.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public AppointmentStatus getStatus() {
        if (cancelled) {
            return AppointmentStatus.CANCELLED;
        }
        if (appointmentDate == null || appointmentTime == null) {
            return null;
        }
        LocalDateTime when = LocalDateTime.of(appointmentDate, appointmentTime);
        return when.isAfter(LocalDateTime.now()) ? AppointmentStatus.UPCOMING : AppointmentStatus.PAST;
    }
}
