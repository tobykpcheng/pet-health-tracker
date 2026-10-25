package com.pethealth.backend.medication;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** A medicine a pet takes. Maps to the MEDICATION table in designs/db-design/pet-health-erd.md. */
@Entity
@Table(name = "medication")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /** The pet taking this medication. Comes from the URL, not the JSON body. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Pet pet;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String medicineName;

    @Size(max = 50)
    @Column(length = 50)
    private String dosage;

    @Size(max = 100)
    @Column(length = 100)
    private String doseFrequency;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    /** Null means the pet is still taking it. */
    private LocalDate endDate;

    /** True when endDate is null or hasn't passed yet. Calculated, not stored. */
    @JsonProperty(value = "isActive", access = JsonProperty.Access.READ_ONLY)
    public boolean isActive() {
        return endDate == null || !endDate.isBefore(LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoseFrequency() {
        return doseFrequency;
    }

    public void setDoseFrequency(String doseFrequency) {
        this.doseFrequency = doseFrequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
