package com.eventhub.event_booking_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String fieldName;

    @Column(nullable = false, length = 2000)
    private String fieldValue;
}
