package com.pethealth.backend.pet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/** A pet. Maps to the PET table in designs/db-design/pet-health-erd.md. */
@Entity
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String species;

    @Size(max = 100)
    @Column(length = 100)
    private String breed;

    @PastOrPresent
    private LocalDate birthDate;

    /** Weight in pounds. */
    @DecimalMin("0.0")
    @Column(name = "weight_lb", precision = 6, scale = 2)
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private Boolean spayedNeutered;

    /** Whole years since birthDate. Calculated, not stored. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Integer getAgeYears() {
        return birthDate == null ? null : Period.between(birthDate, LocalDate.now()).getYears();
    }

    /** Months on top of ageYears. Calculated, not stored. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Integer getAgeMonths() {
        return birthDate == null ? null : Period.between(birthDate, LocalDate.now()).getMonths();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Boolean getSpayedNeutered() {
        return spayedNeutered;
    }

    public void setSpayedNeutered(Boolean spayedNeutered) {
        this.spayedNeutered = spayedNeutered;
    }
}
