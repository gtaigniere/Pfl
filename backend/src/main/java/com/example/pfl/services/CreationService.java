package com.example.pfl.services;

import com.example.pfl.entities.Creation;
import com.example.pfl.repositories.CreationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CreationService {
    private final CreationRepository creationRepository;

    public List<Creation> getAll() {
        return creationRepository.findAll();
    }

    public Creation getById(int id) {
        return creationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Création non trouvée"));
    }

    public void create(Creation creation) {
        creationRepository.save(creation);
    }
}
