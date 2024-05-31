package com.example.pfl.repositories;

import com.example.pfl.entities.Jwt;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {
}
