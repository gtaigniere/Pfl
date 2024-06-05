package com.example.pfl.services;

import com.example.pfl.entities.Role;
import com.example.pfl.entities.RoleType;
import com.example.pfl.entities.User;
import com.example.pfl.entities.Validation;
import com.example.pfl.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ValidationService validationService;

    public void signup(User user) {
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new RuntimeException("Votre mail est invalide");
        }
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new RuntimeException("Votre mail est déjà utilisé");
        }

        String encryptedPassword = bCryptPasswordEncoder.encode(user.getMotDePasse());
        user.setMotDePasse(encryptedPassword);

        Role userRole = new Role();
        userRole.setLibelle(RoleType.UTILISATEUR);
        user.setRole(userRole);

        user = userRepository.save(user);
        validationService.register(user);
    }

    public void activation(Map<String, String> activation) {
        Validation validation = validationService.readAccordingToTheCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration())) {
            throw new RuntimeException("Votre code a expiré");
        }
        User userActivated = userRepository.findById(validation.getUtilisateur().getId()).orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));
        userActivated.setActif(true);
        userRepository.save(userActivated);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne correspond à cet identifiant"));
    }
}
