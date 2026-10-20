package com.pethealth.backend.pet;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pethealth.backend.common.NotFoundException;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pet " + id + " not found"));
    }

    public Pet create(Pet pet) {
        return petRepository.save(pet);
    }

    @Transactional
    public Pet update(Long id, Pet changes) {
        Pet pet = findById(id);
        pet.setName(changes.getName());
        pet.setSpecies(changes.getSpecies());
        pet.setBreed(changes.getBreed());
        pet.setBirthDate(changes.getBirthDate());
        pet.setWeight(changes.getWeight());
        pet.setGender(changes.getGender());
        pet.setSpayedNeutered(changes.getSpayedNeutered());
        return pet;
    }

    public void delete(Long id) {
        petRepository.delete(findById(id));
    }
}
