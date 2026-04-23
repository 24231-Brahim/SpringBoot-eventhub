package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.dto.response.EventSummaryResponse;
import com.eventhub.event_booking_backend.model.Category;
import com.eventhub.event_booking_backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Contrôleur REST pour les événements publics.
 * Permet à tout utilisateur de consulter les événements.
 */
@RestController
@RequestMapping("/api/v1/public/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Récupère la liste de tous les événements.
     * Option de filtrage par catégorie via paramètre de requête.
     * @param category Filtre optionnel par catégorie.
     * @return Une liste de résumés d'événements.
     */
    @GetMapping
    public ResponseEntity<List<EventSummaryResponse>> getAll(@RequestParam(required = false) Category category) {
        return ResponseEntity.ok(eventService.getAllEvents(category));
    }

    /**
     * Récupère les détails d'un événement spécifique.
     * @param id L'ID de l'événement.
     * @return Le résumé de l'événement.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventSummaryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }
}