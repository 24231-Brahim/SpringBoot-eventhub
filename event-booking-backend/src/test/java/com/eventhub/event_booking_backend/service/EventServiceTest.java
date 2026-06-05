package com.eventhub.event_booking_backend.service;

import com.eventhub.event_booking_backend.dto.request.EventCreateRequest;
import com.eventhub.event_booking_backend.dto.request.EventUpdateRequest;
import com.eventhub.event_booking_backend.dto.response.EventSummaryResponse;
import com.eventhub.event_booking_backend.exception.BusinessException;
import com.eventhub.event_booking_backend.model.Category;
import com.eventhub.event_booking_backend.model.Event;
import com.eventhub.event_booking_backend.model.User;
import com.eventhub.event_booking_backend.repository.EventRepository;
import com.eventhub.event_booking_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link EventService}.
 * Vérifie les fonctionnalités de création et récupération des événements.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private User testOrganizer;
    private Event testEvent;

    /**
     * Initialise les données de test avant chaque test.
     */
    @BeforeEach
    void setUp() {
        testOrganizer = User.builder()
                .id(1L)
                .email("organizer@example.com")
                .build();

        testEvent = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Description")
                .category(Category.MUSIC)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .availableSeats(50)
                .price(BigDecimal.valueOf(100))
                .organizer(testOrganizer)
                .build();
    }

    /**
     * Teste la création d'un événement avec succès.
     * Vérifie que la réponse contient les bonnes informations.
     */
    @Test
    void createEvent_Success() {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("New Event");
        request.setDescription("New Description");
        request.setCategory(Category.MUSIC);
        request.setStartDate(LocalDateTime.now().plusDays(1));
        request.setEndDate(LocalDateTime.now().plusDays(2));
        request.setCapacity(100);
        request.setPrice(BigDecimal.valueOf(100));

        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        EventSummaryResponse response = eventService.createEvent(request, "organizer@example.com");

        assertNotNull(response);
        assertEquals("New Event", response.getTitle());
        assertEquals(Category.MUSIC, response.getCategory());
    }

    /**
     * Teste la création d'un événement quand l'organisateur n'existe pas.
     * Doit lever une exception BusinessException.
     */
    @Test
    void createEvent_OrganizerNotFound_ThrowsException() {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("New Event");
        request.setDescription("New Description");
        request.setCategory(Category.MUSIC);
        request.setStartDate(LocalDateTime.now().plusDays(1));
        request.setEndDate(LocalDateTime.now().plusDays(2));
        request.setCapacity(100);
        request.setPrice(BigDecimal.valueOf(100));

        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> eventService.createEvent(request, "organizer@example.com"));
    }

    /**
     * Teste la création d'un événement avec une date de fin antérieure à la date de début.
     * Doit lever une exception BusinessException.
     */
    @Test
    void createEvent_EndDateBeforeStartDate_ThrowsException() {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("New Event");
        request.setDescription("New Description");
        request.setCategory(Category.MUSIC);
        request.setStartDate(LocalDateTime.now().plusDays(2));
        request.setEndDate(LocalDateTime.now().plusDays(1));
        request.setCapacity(100);
        request.setPrice(BigDecimal.valueOf(100));

        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));

        assertThrows(BusinessException.class, () -> eventService.createEvent(request, "organizer@example.com"));
    }

    /**
     * Teste la récupération de tous les événements sans filtre.
     * Retourne tous les événements existants.
     */
    @Test
    void getAllEvents_NoFilter_ReturnsAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(testEvent));

        List<EventSummaryResponse> events = eventService.getAllEvents(null);

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Test Event", events.get(0).getTitle());
    }

    /**
     * Teste la récupération des événements avec filtre par catégorie.
     * Retourne uniquement les événements de la catégorie spécifiée.
     */
    @Test
    void getAllEvents_WithCategoryFilter_ReturnsFilteredEvents() {
        when(eventRepository.findByCategory(Category.MUSIC)).thenReturn(List.of(testEvent));

        List<EventSummaryResponse> events = eventService.getAllEvents(Category.MUSIC);

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(Category.MUSIC, events.get(0).getCategory());
    }

    /**
     * Teste la récupération d'un événement par son ID avec succès.
     * Retourne les détails de l'événement trouvé.
     */
    @Test
    void getById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        EventSummaryResponse response = eventService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Event", response.getTitle());
    }

    /**
     * Teste la récupération d'un événement par son ID quand l'événement n'existe pas.
     * Doit lever une exception BusinessException.
     */
    @Test
    void getById_EventNotFound_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> eventService.getById(1L));
    }

    @Test
    void updateEvent_Success() {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setTitle("Updated Event");
        request.setDescription("Updated Description");
        request.setCategory(Category.TECH);
        request.setStartDate(LocalDateTime.now().plusDays(3));
        request.setEndDate(LocalDateTime.now().plusDays(5));
        request.setCapacity(200);
        request.setPrice(BigDecimal.valueOf(150));

        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventSummaryResponse response = eventService.updateEvent(1L, request, "organizer@example.com");

        assertNotNull(response);
        assertEquals("Updated Event", response.getTitle());
        assertEquals(Category.TECH, response.getCategory());
        assertEquals(200, response.getCapacity());
        assertEquals(BigDecimal.valueOf(150), response.getPrice());
    }

    @Test
    void updateEvent_OrganizerNotFound_ThrowsException() {
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> eventService.updateEvent(1L, new EventUpdateRequest(), "organizer@example.com"));
    }

    @Test
    void updateEvent_EventNotFound_ThrowsException() {
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> eventService.updateEvent(1L, new EventUpdateRequest(), "organizer@example.com"));
    }

    @Test
    void updateEvent_NotOwner_ThrowsException() {
        User otherOrganizer = User.builder().id(99L).email("other@example.com").build();

        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        assertThrows(BusinessException.class,
                () -> eventService.updateEvent(1L, new EventUpdateRequest(), "other@example.com"));
    }

    @Test
    void updateEvent_EndDateBeforeStartDate_ThrowsException() {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setEndDate(LocalDateTime.now().plusDays(1));
        request.setStartDate(LocalDateTime.now().plusDays(3));

        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        assertThrows(BusinessException.class,
                () -> eventService.updateEvent(1L, request, "organizer@example.com"));
    }

    @Test
    void updateEvent_CannotReduceBelowBooked_ThrowsException() {
        testEvent.setCapacity(100);
        testEvent.setAvailableSeats(5);
        EventUpdateRequest request = new EventUpdateRequest();
        request.setTitle("Event");
        request.setDescription("Desc");
        request.setCategory(Category.MUSIC);
        request.setStartDate(LocalDateTime.now().plusDays(1));
        request.setEndDate(LocalDateTime.now().plusDays(2));
        request.setCapacity(3);
        request.setPrice(BigDecimal.TEN);

        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        assertThrows(BusinessException.class,
                () -> eventService.updateEvent(1L, request, "organizer@example.com"));
    }

    @Test
    void deleteEvent_Success() {
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        eventService.deleteEvent(1L, "organizer@example.com");

        verify(eventRepository).delete(testEvent);
    }

    @Test
    void deleteEvent_OrganizerNotFound_ThrowsException() {
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> eventService.deleteEvent(1L, "organizer@example.com"));
    }

    @Test
    void deleteEvent_EventNotFound_ThrowsException() {
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(Optional.of(testOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> eventService.deleteEvent(1L, "organizer@example.com"));
    }

    @Test
    void deleteEvent_NotOwner_ThrowsException() {
        User otherOrganizer = User.builder().id(99L).email("other@example.com").build();

        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherOrganizer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        assertThrows(BusinessException.class, () -> eventService.deleteEvent(1L, "other@example.com"));
    }
}