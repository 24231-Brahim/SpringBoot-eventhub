package com.eventhub.event_booking_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingCreateRequest {
    @NotNull
    private Long eventId;
}
