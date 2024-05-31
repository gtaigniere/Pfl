package com.example.pfl.security;

import com.example.pfl.entities.Jwt;
import com.example.pfl.entities.User;
import com.example.pfl.repositories.JwtRepository;
import com.example.pfl.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JwtService {
    private static final String BEARER = "bearer";
    private final String ENCRIPTION_KEY = "695801124cce09e21d5aa458c9b84c8f0fc53e55d2850313346239d9adfa6dae";
    private UserService userService;
    private JwtRepository jwtRepository;

//    @Value("${token.signing.key}")
//    private String jwtSigningKey;
//
//    @Value("${token.duration}")
//    private long jwtDuration;

    public Map<String, String> generate(String username) {
        User user = this.userService.loadUserByUsername(username);
        final Map<String, String> jwtMap = this.generateJwt(user);

        final Jwt jwt = Jwt.builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .utilisateur(user)
                .build();
        this.jwtRepository.save(jwt);
        return jwtMap;
    }

    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, String> generateJwt(User user) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000;

        final Map<String, Object> claims = Map.of(
                "pseudo", user.getPseudo(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, user.getEmail()
        );

        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(user.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of(BEARER, bearer);
    }

    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }
}
