package com.example.pfl.security;

import com.example.pfl.entities.Jwt;
import com.example.pfl.entities.RefreshToken;
import com.example.pfl.entities.User;
import com.example.pfl.repositories.JwtRepository;
import com.example.pfl.repositories.RefreshTokenRepository;
import com.example.pfl.services.AccountService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class JwtService {
    private static final String BEARER = "bearer";
    public static final String REFRESH = "refresh";
    private final String ENCRIPTION_KEY = "695801124cce09e21d5aa458c9b84c8f0fc53e55d2850313346239d9adfa6dae";
    private AccountService accountService;
    private JwtRepository jwtRepository;
    private RefreshTokenRepository refreshTokenRepository;

    public Jwt tokenByValue(String value) {
        if (this.jwtRepository.findByValeurAndDesactiveAndExpire(value, true, true).isPresent()) {
            throw new RuntimeException("Le token n'est plus valide");
        }
        return this.jwtRepository.findByValeurAndDesactiveAndExpire(value, false, false).orElseThrow(() -> new RuntimeException("Token inconnu"));
    }

//    @Value("${token.signing.key}")
//    private String jwtSigningKey;
//
//    @Value("${token.duration}")
//    private long jwtDuration;

    public Map<String, String> generate(String username) {
        User user = this.accountService.loadUserByUsername(username);
        this.disabledTokens(user);
        final Map<String, String> jwtMap = new java.util.HashMap<>(this.generateJwt(user));

        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(10 * 60 * 1000))
                .build();

        final Jwt jwt = Jwt.builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .utilisateur(user)
                .refreshToken(refreshToken)
                .build();
        this.jwtRepository.save(jwt);
        jwtMap.put(REFRESH, refreshToken.getValeur());
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
        final long expirationTime = currentTime + 2 * 60 * 1000;

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

    public void logout() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt = this.jwtRepository.findUtilisateurValidToken(user.getEmail(), false, false).orElseThrow(() -> new RuntimeException("Token invalide"));
        jwt.setDesactive(true);
        jwt.setExpire(true);
        this.jwtRepository.save(jwt);
    }

    private void disabledTokens(User user) {
        final List<Jwt> jwtList = this.jwtRepository.findUtilisateur(user.getEmail()).peek(
                jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpire(true);
                }
        ).toList();

        this.jwtRepository.saveAll(jwtList);
    }

    public Map<String, String> refreshToken(Map<String, String> refreshTokenRequest) {
        final Jwt jwt = this.jwtRepository.findByRefreshToken(refreshTokenRequest.get(REFRESH)).orElseThrow(() -> new RuntimeException("Token non trouvé"));
        if (jwt.getRefreshToken().isExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())) {
            RefreshToken refreshToken = this.refreshTokenRepository.findByValeur(refreshTokenRequest.get(REFRESH)).orElseThrow(() -> new RuntimeException("RefreshToken non trouvé"));
            refreshToken.setExpire(true);
            this.refreshTokenRepository.save(refreshToken);
            throw new RuntimeException("RefreshToken expiré");
        }
        this.disabledTokens(jwt.getUtilisateur());
        return this.generate(jwt.getUtilisateur().getEmail());
    }

    public List<RefreshToken> getAll() {
        return (List<RefreshToken>) this.refreshTokenRepository.findAll();
    }

    public void delete(Integer id) {
        this.refreshTokenRepository.deleteById(id);
    }

    //    @Scheduled(cron = "@daily")
    @Scheduled(cron = "0 */1 * * * *")
    public void removeUselessJwt() {
        log.info("Suppression des tokens à {}", Instant.now());
        this.jwtRepository.deleteAllByDesactiveAndExpire(true, true);
    }

//    @Scheduled(cron = "0 */1 * * * *")
    /*
    public void removeUselessRefreshToken() {
        log.info("Suppression des refreshTokens à {}", Instant.now());
        List<RefreshToken> refreshTokenList = this.getAll();
        refreshTokenList.forEach(refreshToken -> {
            if (refreshToken.getExpiration().isBefore(Instant.now())) {
                this.refreshTokenRepository.deleteById(refreshToken.getId());
            }
        });
    }
    */
}
