package com.eventhub.event_booking_backend.exception;

/**
 * Exception personnalisée pour les erreurs métier.
 * Utilisée pour représenter les erreurs fonctionnelles du domaine.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}