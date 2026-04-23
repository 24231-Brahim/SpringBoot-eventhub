package com.eventhub.event_booking_backend.model;

/**
 * Énumération des rôles utilisateur dans le système.
 * ROLE_USER: Utilisateur standard pouvant réserver des événements.
 * ROLE_ORGANIZER: Utilisateur pouvant créer des événements.
 * ROLE_ADMIN: Administrateur avec accès complet.
 */
public enum Role {
    ROLE_USER,
    ROLE_ORGANIZER,
    ROLE_ADMIN
}
