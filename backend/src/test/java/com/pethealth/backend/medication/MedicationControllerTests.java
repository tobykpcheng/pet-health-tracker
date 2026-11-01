package com.pethealth.backend.medication;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.pethealth.backend.pet.Pet;
import com.pethealth.backend.pet.PetRepository;

/** Exercises the /pets/{pet_id}/medication endpoints against an in-memory H2 database. */
@SpringBootTest
@AutoConfigureMockMvc
class MedicationControllerTests {

    private static final String SIMPARICA = """
            {"medicineName": "Simparica Trio", "dosage": "30 mg", "directionsForUse": "Every 30 days",
             "startDate": "2025-07-15", "endDate": null}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    private Long petId;

    @BeforeEach
    void createPet() {
        petRepository.deleteAll();
        Pet pet = new Pet();
        pet.setName("Maru");
        pet.setSpecies("Dog");
        petId = petRepository.save(pet).getId();
    }

    private String url() {
        return "/pets/" + petId + "/medication";
    }

    private Long saveMedication(Long forPetId, LocalDate startDate, LocalDate endDate) {
        Medication medication = new Medication();
        medication.setPet(petRepository.findById(forPetId).orElseThrow());
        medication.setMedicineName("Apoquel");
        medication.setStartDate(startDate);
        medication.setEndDate(endDate);
        return medicationRepository.save(medication).getId();
    }

    @Test
    void createReturns201WithIdAndIsActive() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON).content(SIMPARICA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.medicineName").value("Simparica Trio"))
                .andExpect(jsonPath("$.directionsForUse").value("Every 30 days"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void createWithoutMedicineNameReturns400() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2025-07-15\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithoutStartDateReturns400() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"medicineName\": \"Apoquel\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void isActiveFollowsEndDate() throws Exception {
        LocalDate today = LocalDate.now();
        Long ended = saveMedication(petId, today.minusMonths(2), today.minusDays(1));
        Long endsToday = saveMedication(petId, today.minusMonths(1), today);

        mockMvc.perform(get(url() + "/" + ended)).andExpect(jsonPath("$.isActive").value(false));
        mockMvc.perform(get(url() + "/" + endsToday)).andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void listIsNewestFirstThenGetUpdateAndDelete() throws Exception {
        saveMedication(petId, LocalDate.of(2024, 1, 1), null);
        Long id = saveMedication(petId, LocalDate.of(2025, 1, 1), null);

        mockMvc.perform(get(url()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].startDate").value("2025-01-01"));

        mockMvc.perform(get(url() + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicineName").value("Apoquel"));

        mockMvc.perform(put(url() + "/" + id).contentType(MediaType.APPLICATION_JSON).content(SIMPARICA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.medicineName").value("Simparica Trio"))
                .andExpect(jsonPath("$.dosage").value("30 mg"));

        mockMvc.perform(delete(url() + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(url() + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingPetOrMedicationReturns404() throws Exception {
        mockMvc.perform(get("/pets/999/medication")).andExpect(status().isNotFound());
        mockMvc.perform(post("/pets/999/medication").contentType(MediaType.APPLICATION_JSON).content(SIMPARICA))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(url() + "/999")).andExpect(status().isNotFound());
        mockMvc.perform(put(url() + "/999").contentType(MediaType.APPLICATION_JSON).content(SIMPARICA))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(url() + "/999")).andExpect(status().isNotFound());
    }

    @Test
    void medicationOfAnotherPetReturns404() throws Exception {
        Pet other = new Pet();
        other.setName("Mochi");
        other.setSpecies("Cat");
        Long otherMedicationId = saveMedication(petRepository.save(other).getId(), LocalDate.of(2025, 1, 1), null);

        mockMvc.perform(get(url() + "/" + otherMedicationId)).andExpect(status().isNotFound());
    }

    @Test
    void deletingPetDeletesItsMedications() throws Exception {
        saveMedication(petId, LocalDate.of(2025, 1, 1), null);

        mockMvc.perform(delete("/pets/" + petId)).andExpect(status().isNoContent());

        assertEquals(0, medicationRepository.count());
    }
}
