package com.example.pfl.services;

import com.example.pfl.entities.User;
import com.example.pfl.entities.Validation;
import com.example.pfl.repositories.ValidationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class ValidationService {
    private ValidationRepository validationRepository;
    private NotificationService notificationService;

    public void register(User user) {
        Validation validation = new Validation();
        validation.setUtilisateur(user);
        Instant creation = Instant.now();
        validation.setCreation(creation);

        Instant expiration = creation.plus(10, MINUTES);
        validation.setExpiration(expiration);

        Random random = new Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d", randomInteger);

        validation.setCode(code);
        validationRepository.save(validation);
        notificationService.send(validation);
    }

    public Validation readAccordingToTheCode(String code) {
        return validationRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Votre code est invalide"));
    }

    // Exécution toutes les 30 secondes
    @Scheduled(cron = "*/30 * * * * *")
    public void removeUselessValidationCode() {
        log.info("Suppression des codes de validation à {}", Instant.now());
        this.validationRepository.deleteAllByExpirationBefore(Instant.now());
    }
}
