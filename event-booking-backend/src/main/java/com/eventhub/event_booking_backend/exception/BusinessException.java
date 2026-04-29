package com.eventhub.event_booking_backend.exception;

/**
 * Exception métier personnalisée pour l'application
 * Utilisée pour signaler des erreurs liées à la logique métier
 */
public class BusinessException extends RuntimeException {
    /**
     * Constructeur avec message d'erreur
     * @param message Le message d'erreur descriptif
     */
    public BusinessException(String message) {
        super(message);
    }
}
