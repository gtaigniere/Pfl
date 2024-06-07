package com.example.pfl.controllers;

import com.example.pfl.dtos.AuthenticationDTO;
import com.example.pfl.entities.User;
import com.example.pfl.security.JwtService;
import com.example.pfl.services.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "auth")
public class AccountController {
    private AuthenticationManager authenticationManager;
    private AccountService accountService;
    private JwtService jwtService;

    @PostMapping(path = "signup")
    public void signup(@RequestBody User user) {
        log.info("Inscription");
        accountService.signup(user);
    }

    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String, String> activation) {
        log.info("Activation");
        accountService.activation(activation);
    }

    @PostMapping(path = "password-change")
    public void passwordChange(@RequestBody Map<String, String> parameters) {
        log.info("Changement de mot de passe");
        accountService.passwordChange(parameters);
    }

    @PostMapping(path = "password-new")
    public void passwordNew(@RequestBody Map<String, String> parameters) {
        log.info("Nouveau mot de passe");
        accountService.passwordNew(parameters);
    }

    @PostMapping(path = "refresh-token")
    public @ResponseBody Map<String, String> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        log.info("Rafraichissement token");
        return this.jwtService.refreshToken(refreshTokenRequest);
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
        log.info("Deconnexion");
        jwtService.logout();
    }
}
