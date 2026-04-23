package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.dto.request.EventCreateRequest;
import com.eventhub.event_booking_backend.dto.response.EventSummaryResponse;
import com.eventhub.event_booking_backend.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour les organisers.
 * Gère la création d'événements (accessible aux rôles ORGANIZER et ADMIN).
 */
@RestController
@RequestMapping("/api/v1/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final EventService eventService;

    /**
     * Crée un nouvel événement.
     * L'organisateur est automatiquement récupéré depuis le token JWT.
     * @param request Les données de l'événement.
     * @param authentication L'authentification contenant l'email de l'organisateur.
     * @return Les détails de l'événement créé.
     */
    @PostMapping("/events")
    public ResponseEntity<EventSummaryResponse> createEvent(@Valid @RequestBody EventCreateRequest request,
                                                            Authentication authentication) {
        return ResponseEntity.ok(eventService.createEvent(request, authentication.getName()));
    }
}