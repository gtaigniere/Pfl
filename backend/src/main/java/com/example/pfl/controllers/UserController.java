package com.example.pfl.controllers;

import com.example.pfl.dtos.AuthenticationDTO;
import com.example.pfl.entities.User;
import com.example.pfl.security.JwtService;
import com.example.pfl.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtService jwtService;

    @PostMapping(path = "signup")
    public void signup(@RequestBody User user) {
        log.info("Inscription");
        userService.signup(user);
    }

    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String, String> activation) {
        log.info("Activation");
        userService.activation(activation);
    }

    @PostMapping(path = "login")
    public Map<String, String> login(@RequestBody AuthenticationDTO authenticationDTO) {
        log.info("Connexion");
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.motDePasse())
        );
        if (authenticate.isAuthenticated()) {
            return jwtService.generate(authenticationDTO.email());
        }
        return null;
    }

    @PostMapping(path = "logout")
    public void logout() {
        log.info("Déconnexion");
        jwtService.logout();
    }
}
