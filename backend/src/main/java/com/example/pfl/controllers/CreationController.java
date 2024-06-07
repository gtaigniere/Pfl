package com.example.pfl.controllers;

import com.example.pfl.entities.Creation;
import com.example.pfl.services.CreationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("creations")
public class CreationController {
    private final CreationService creationService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<Creation> getAll() {
        return creationService.getAll();
    }

    @GetMapping("/{id}")
    public Creation getById(@PathVariable int id) {
        return creationService.getById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(@RequestBody Creation creation) {
        creationService.create(creation);
    }
}
