package com.eventhub.event_booking_backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour les informations d'une réservation.
 * Contient les détails de la réservation et de l'événement associé.
 */
@Data
@Builder
public class BookingResponse {
    private Long bookingId;
    private Long eventId;
    private String eventTitle;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private LocalDateTime createdAt;
}