package com.pethealth.backend.pet;

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

/** The /pets endpoints from designs/api-design/pet-health-api.yaml. */
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<Pet> list() {
        return petService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet create(@Valid @RequestBody Pet pet) {
        return petService.create(pet);
    }

    @GetMapping("/{id}")
    public Pet get(@PathVariable Long id) {
        return petService.findById(id);
    }

    @PutMapping("/{id}")
    public Pet update(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        return petService.update(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        petService.delete(id);
    }
}
