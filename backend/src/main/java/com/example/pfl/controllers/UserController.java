package com.example.pfl.controllers;

import com.example.pfl.entities.User;
import com.example.pfl.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "users")
public class UserController {
    private final UserService userService;

//    @PreAuthorize("hasAuthority('ADMIN_READ')")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }
}
