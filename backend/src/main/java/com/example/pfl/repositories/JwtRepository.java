package com.example.pfl.repositories;

import com.example.pfl.entities.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {

    Optional<Jwt> findByValeurAndDesactiveAndExpire(String value, boolean desactive, boolean expire);

    @Query("FROM Jwt j WHERE j.utilisateur.email = :email AND j.desactive = :desactive AND j.expire = :expire")
    Optional<Jwt> findUtilisateurValidToken(String email, boolean desactive, boolean expire);

    @Query("FROM Jwt j WHERE j.utilisateur.email = :email")
    Stream<Jwt> findUtilisateur(String email);

    void deleteAllByDesactiveAndExpire(boolean desactive, boolean expire);
}
