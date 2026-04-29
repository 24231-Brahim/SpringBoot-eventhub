package com.eventhub.event_booking_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de requête pour la création d'une réservation.
 * Contient l'ID de l'événement à réserver.
 */
@Data
public class BookingCreateRequest {
    @NotNull(message = "L'ID de l'événement est obligatoire")
    private Long eventId;
}
