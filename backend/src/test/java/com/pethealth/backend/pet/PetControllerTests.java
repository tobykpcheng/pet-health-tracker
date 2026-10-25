package com.pethealth.backend.pet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
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

/** Exercises the /pets endpoints against an in-memory H2 database. */
@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTests {

    private static final String MARU = """
            {"name": "Maru", "species": "Dog", "breed": "Maltipoo", "birthDate": "2022-11-12",
             "weight": 10, "gender": "MALE", "spayedNeutered": true}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void clearPets() {
        petRepository.deleteAll();
    }

    @Test
    void createReturns201WithIdAndAge() throws Exception {
        mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(MARU))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Maru"))
                .andExpect(jsonPath("$.weight").value(10))
                .andExpect(jsonPath("$.ageYears", notNullValue()))
                .andExpect(jsonPath("$.ageMonths", notNullValue()));
    }

    @Test
    void createWithoutNameReturns400() throws Exception {
        mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"species\": \"Cat\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidGenderReturns400() throws Exception {
        mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Mochi\", \"species\": \"Cat\", \"gender\": \"UNKNOWN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listGetUpdateAndDelete() throws Exception {
        Pet pet = new Pet();
        pet.setName("Maru");
        pet.setSpecies("Dog");
        pet.setBirthDate(LocalDate.of(2022, 11, 12));
        Long id = petRepository.save(pet).getId();

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/pets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.species").value("Dog"));

        mockMvc.perform(put("/pets/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Maru\", \"species\": \"Dog\", \"weight\": 11.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.weight").value(11.5));

        mockMvc.perform(delete("/pets/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/pets/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingPetReturns404() throws Exception {
        mockMvc.perform(get("/pets/999")).andExpect(status().isNotFound());
        mockMvc.perform(put("/pets/999").contentType(MediaType.APPLICATION_JSON).content(MARU))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/pets/999")).andExpect(status().isNotFound());
    }
}
