package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.dto.response.BookingResponse;
import com.eventhub.event_booking_backend.dto.response.EventSummaryResponse;
import com.eventhub.event_booking_backend.model.User;
import com.eventhub.event_booking_backend.repository.UserRepository;
import com.eventhub.event_booking_backend.service.BookingService;
import com.eventhub.event_booking_backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EventService eventService;
    private final BookingService bookingService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventSummaryResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents(null));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.getById(id);
        return ResponseEntity.noContent().build();
    }
}
