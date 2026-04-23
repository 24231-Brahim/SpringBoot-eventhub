package com.eventhub.event_booking_backend.model;

/**
 * Énumération des statuts de paiement pour une réservation.
 * PENDING: En attente de paiement.
 * PAID: Paiement reçu.
 * COMPLETED: Réservation finalisée.
 * CANCELLED: Réservation annulée.
 */
public enum PaymentStatus {
    PENDING,
    PAID,
    COMPLETED,
    CANCELLED
}
