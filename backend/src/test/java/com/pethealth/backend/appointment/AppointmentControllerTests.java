package com.pethealth.backend.appointment;

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
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.pethealth.backend.pet.Pet;
import com.pethealth.backend.pet.PetRepository;

/** Exercises the /pets/{pet_id}/appointment endpoints against an in-memory H2 database. */
@SpringBootTest
@AutoConfigureMockMvc
class AppointmentControllerTests {

    private static final String RABIES_SHOT = """
            {"appointmentDate": "2025-09-15", "appointmentTime": "13:30",
             "clinicName": "Burquitlam Animal Hospital", "visitReason": "Vaccination",
             "notes": "Rabies vaccination"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

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
        return "/pets/" + petId + "/appointment";
    }

    private Long saveAppointment(Long forPetId, LocalDate date, LocalTime time) {
        Appointment appointment = new Appointment();
        appointment.setPet(petRepository.findById(forPetId).orElseThrow());
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        return appointmentRepository.save(appointment).getId();
    }

    @Test
    void createReturns201WithIdAndHhMmTime() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON).content(RABIES_SHOT))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.appointmentDate").value("2025-09-15"))
                .andExpect(jsonPath("$.appointmentTime").value("13:30"))
                .andExpect(jsonPath("$.cancelled").value(false))
                .andExpect(jsonPath("$.status").value("PAST"));
    }

    @Test
    void futureAppointmentIsUpcomingAndSentStatusIsIgnored() throws Exception {
        String nextYear = LocalDate.now().plusYears(1).toString();
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"appointmentDate\": \"" + nextYear + "\", \"appointmentTime\": \"09:00\","
                                + " \"status\": \"PAST\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("UPCOMING"));
    }

    @Test
    void cancelledAppointmentIsCancelled() throws Exception {
        String nextYear = LocalDate.now().plusYears(1).toString();
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"appointmentDate\": \"" + nextYear + "\", \"appointmentTime\": \"09:00\","
                                + " \"cancelled\": true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cancelled").value(true))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void createWithInvalidTimeReturns400() throws Exception {
        mockMvc.perform(post(url()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"appointmentDate\": \"2025-09-15\", \"appointmentTime\": \"25:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listIsSoonestFirstThenGetUpdateAndDelete() throws Exception {
        Long id = saveAppointment(petId, LocalDate.of(2025, 9, 15), LocalTime.of(15, 0));
        saveAppointment(petId, LocalDate.of(2025, 9, 15), LocalTime.of(9, 0));

        mockMvc.perform(get(url()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].appointmentTime").value("09:00"));

        mockMvc.perform(get(url() + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAST"));

        mockMvc.perform(put(url() + "/" + id).contentType(MediaType.APPLICATION_JSON).content(RABIES_SHOT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.appointmentTime").value("13:30"))
                .andExpect(jsonPath("$.status").value("PAST"));

        mockMvc.perform(delete(url() + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(url() + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingPetOrAppointmentReturns404() throws Exception {
        mockMvc.perform(get("/pets/999/appointment")).andExpect(status().isNotFound());
        mockMvc.perform(post("/pets/999/appointment").contentType(MediaType.APPLICATION_JSON).content(RABIES_SHOT))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(url() + "/999")).andExpect(status().isNotFound());
        mockMvc.perform(put(url() + "/999").contentType(MediaType.APPLICATION_JSON).content(RABIES_SHOT))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(url() + "/999")).andExpect(status().isNotFound());
    }

    @Test
    void appointmentOfAnotherPetReturns404() throws Exception {
        Pet other = new Pet();
        other.setName("Mochi");
        other.setSpecies("Cat");
        Long otherAppointmentId = saveAppointment(petRepository.save(other).getId(),
                LocalDate.of(2025, 9, 15), LocalTime.of(9, 0));

        mockMvc.perform(get(url() + "/" + otherAppointmentId)).andExpect(status().isNotFound());
    }

    @Test
    void deletingPetDeletesItsAppointments() throws Exception {
        saveAppointment(petId, LocalDate.of(2025, 9, 15), LocalTime.of(9, 0));

        mockMvc.perform(delete("/pets/" + petId)).andExpect(status().isNoContent());

        assertEquals(0, appointmentRepository.count());
    }
}
