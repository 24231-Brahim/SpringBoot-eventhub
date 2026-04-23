package com.eventhub.event_booking_backend.dto.response;

import com.eventhub.event_booking_backend.model.Category;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour les informations sommaires d'un événement.
 * Utilisé pour les listings et détails d'événements.
 */
@Data
@Builder
public class EventSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
    private Integer availableSeats;
    private BigDecimal price;
    private Long organizerId;
}