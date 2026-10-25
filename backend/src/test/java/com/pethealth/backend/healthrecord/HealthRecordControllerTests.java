package com.pethealth.backend.healthrecord;

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

/** Exercises the /pets/{pet_id}/health_record endpoints against an in-memory H2 database. */
@SpringBootTest
@AutoConfigureMockMvc
class HealthRecordControllerTests {

    private static final String CHECK_UP = """
            {"recordDate": "2025-07-15", "recordType": "CHECK_UP",
             "clinicName": "A Niu Veterinary Hospital", "notes": "General examination"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

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
        return "/pets/" + petId + "/health_record";
    }

    private Long saveRecord(Long forPetId, LocalDate date) {
        HealthRecord record = new HealthRecord();
        record.setPet(petRepository.findById(forPetId).orElseThrow());
        record.setRecordDate(date);
        record.setRecordType(RecordType.VACCINATION);
        return healthRecordRepository.save(record).getId();
    }

    @Test
    void createReturns201WithId() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON).content(CHECK_UP))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.recordDate").value("2025-07-15"))
                .andExpect(jsonPath("$.recordType").value("CHECK_UP"))
                .andExpect(jsonPath("$.clinicName").value("A Niu Veterinary Hospital"));
    }

    @Test
    void createWithoutRecordTypeReturns400() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recordDate\": \"2025-07-15\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidRecordTypeReturns400() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recordDate\": \"2025-07-15\", \"recordType\": \"GROOMING\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listIsNewestFirstThenGetUpdateAndDelete() throws Exception {
        saveRecord(petId, LocalDate.of(2024, 1, 1));
        Long id = saveRecord(petId, LocalDate.of(2025, 1, 1));

        mockMvc.perform(get(url()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].recordDate").value("2025-01-01"));

        mockMvc.perform(get(url() + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordType").value("VACCINATION"));

        mockMvc.perform(put(url() + "/" + id).contentType(MediaType.APPLICATION_JSON).content(CHECK_UP))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.recordType").value("CHECK_UP"))
                .andExpect(jsonPath("$.notes").value("General examination"));

        mockMvc.perform(delete(url() + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(url() + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingPetOrRecordReturns404() throws Exception {
        mockMvc.perform(get("/pets/999/health_record")).andExpect(status().isNotFound());
        mockMvc.perform(post("/pets/999/health_record").contentType(MediaType.APPLICATION_JSON).content(CHECK_UP))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(url() + "/999")).andExpect(status().isNotFound());
        mockMvc.perform(put(url() + "/999").contentType(MediaType.APPLICATION_JSON).content(CHECK_UP))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(url() + "/999")).andExpect(status().isNotFound());
    }

    @Test
    void recordOfAnotherPetReturns404() throws Exception {
        Pet other = new Pet();
        other.setName("Mochi");
        other.setSpecies("Cat");
        Long otherRecordId = saveRecord(petRepository.save(other).getId(), LocalDate.of(2025, 1, 1));

        mockMvc.perform(get(url() + "/" + otherRecordId)).andExpect(status().isNotFound());
    }

    @Test
    void deletingPetDeletesItsRecords() throws Exception {
        saveRecord(petId, LocalDate.of(2025, 1, 1));

        mockMvc.perform(delete("/pets/" + petId)).andExpect(status().isNoContent());

        assertEquals(0, healthRecordRepository.count());
    }
}
