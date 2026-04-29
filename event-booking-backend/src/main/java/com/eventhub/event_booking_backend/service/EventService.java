package com.eventhub.event_booking_backend.service;

import com.eventhub.event_booking_backend.dto.request.EventCreateRequest;
import com.eventhub.event_booking_backend.dto.response.EventSummaryResponse;
import com.eventhub.event_booking_backend.exception.BusinessException;
import com.eventhub.event_booking_backend.model.Category;
import com.eventhub.event_booking_backend.model.Event;
import com.eventhub.event_booking_backend.model.User;
import com.eventhub.event_booking_backend.repository.EventRepository;
import com.eventhub.event_booking_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service de gestion des événements.
 * Gère la création, la récupération et le filtrage des événements.
 */
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * Crée un nouvel événement.
     * Initialise availableSeats avec la capacité totale.
     * @param request Les données de l'événement à créer.
     * @param organizerEmail L'email de l'organisateur.
     * @return Les détails de l'événement créé.
     * @throws BusinessException si l'organisateur n'existe pas ou si la date de fin est antérieure à la date de début.
     */
    public EventSummaryResponse createEvent(EventCreateRequest request, String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new BusinessException("Organizer not found"));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date must be after start date");
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .capacity(request.getCapacity())
                .availableSeats(request.getCapacity())
                .price(request.getPrice())
                .organizer(organizer)
                .build();

        return mapToResponse(eventRepository.save(event));
    }

    /**
     * Récupère tous les événements, avec filtrage optionnel par catégorie.
     * @param category La catégorie à filtrer (null pour tous).
     * @return Une liste de résumés d'événements.
     */
    public List<EventSummaryResponse> getAllEvents(Category category) {
        List<Event> events = category == null ? eventRepository.findAll() : eventRepository.findByCategory(category);
        return events.stream().map(this::mapToResponse).toList();
    }

    /**
     * Récupère un événement par son ID.
     * @param id L'ID de l'événement.
     * @return Le résumé de l'événement.
     * @throws BusinessException si l'événement n'existe pas.
     */
    public EventSummaryResponse getById(Long id) {
        return mapToResponse(eventRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Event not found")));
    }

    /**
     * Convertit une entité Event en EventSummaryResponse.
     * @param event L'entité à convertir.
     * @return Le DTO de réponse.
     */
    public EventSummaryResponse mapToResponse(Event event) {
        return EventSummaryResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .capacity(event.getCapacity())
                .availableSeats(event.getAvailableSeats())
                .price(event.getPrice())
                .organizerId(event.getOrganizer().getId())
                .build();
    }
}