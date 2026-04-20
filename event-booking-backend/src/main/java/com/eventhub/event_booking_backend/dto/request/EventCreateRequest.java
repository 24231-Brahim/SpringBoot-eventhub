package com.eventhub.event_booking_backend.dto.request;

import com.eventhub.event_booking_backend.model.Category;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EventCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Category category;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull @Min(1)
    private Integer capacity;

    @NotNull @DecimalMin("0.0")
    private BigDecimal price;
}