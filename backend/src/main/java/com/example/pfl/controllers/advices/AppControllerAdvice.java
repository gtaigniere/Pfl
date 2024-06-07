package com.example.pfl.controllers.advices;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
public class AppControllerAdvice {

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = Exception.class)
    public Map<String, String> exceptionsHandler() {
        return Map.of("erreur", "description");
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = BadCredentialsException.class)
    public @ResponseBody ProblemDetail badCredentialsException(final BadCredentialsException exception) {
        log.error(exception.getMessage(), exception);
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, "Identifiants invalides");
        problemDetail.setProperty("erreur", "Nous n'avons pas pu vous identifier");
        return problemDetail;
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = {SignatureException.class, MalformedJwtException.class})
    public @ResponseBody ProblemDetail badCredentialsException(final Exception exception) {
        log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(UNAUTHORIZED, "Token invalide");
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public @ResponseBody ProblemDetail badCredentialsException(final AccessDeniedException exception) {
        log.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(FORBIDDEN, "Vos droits ne vous permettent pas d'effectuer cette action");
    }
}
