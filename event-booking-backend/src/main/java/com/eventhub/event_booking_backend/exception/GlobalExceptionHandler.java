package com.eventhub.event_booking_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gestionnaire global d'exceptions pour l'application
 * Intercepte les exceptions et renvoie des réponses HTTP appropriées
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les exceptions métier (BusinessException)
     * @param ex L'exception métier levée
     * @return Une réponse avec le code 400 (Bad Request) et le message d'erreur
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Gère les exceptions d'authentification (mauvais identifiants)
     * @return Une réponse avec le code 401 (Unauthorized) et un message d'erreur
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException() {
        ErrorResponse error = new ErrorResponse("Identifiants invalides");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Classe interne pour la réponse d'erreur
     * @param error Le message d'erreur
     */
    record ErrorResponse(String error) {}
}
