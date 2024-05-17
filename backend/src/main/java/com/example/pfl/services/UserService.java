package com.example.pfl.services;

import com.example.pfl.entities.Role;
import com.example.pfl.entities.RoleType;
import com.example.pfl.entities.User;
import com.example.pfl.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ValidationService validationService;

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
}
