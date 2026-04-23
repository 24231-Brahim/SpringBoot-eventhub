package com.eventhub.event_booking_backend.service;

import com.eventhub.event_booking_backend.dto.request.BookingCreateRequest;
import com.eventhub.event_booking_backend.dto.response.BookingResponse;
import com.eventhub.event_booking_backend.exception.BusinessException;
import com.eventhub.event_booking_backend.model.Booking;
import com.eventhub.event_booking_backend.model.Event;
import com.eventhub.event_booking_backend.model.PaymentStatus;
import com.eventhub.event_booking_backend.model.User;
import com.eventhub.event_booking_backend.repository.BookingRepository;
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
 * Tests unitaires pour {@link BookingService}.
 * Vérifie les fonctionnalités de création et récupération des réservations.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Event testEvent;

    /**
     * Initialise les données de test avant chaque test.
     */
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        testEvent = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Description")
                .capacity(100)
                .availableSeats(50)
                .price(BigDecimal.valueOf(100))
                .build();
    }

    /**
     * Teste la création d'une réservation avec succès.
     * Vérifie que la réponse contient les bonnes informations et le statut PENDING.
     */
    @Test
    void createBooking_Success() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(1L);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(testEvent));
        when(bookingRepository.existsByEventIdAndUserId(1L, 1L)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.createBooking(request, "test@example.com");

        assertNotNull(response);
        assertEquals(1L, response.getEventId());
        assertEquals("Test Event", response.getEventTitle());
        assertEquals(BigDecimal.valueOf(100), response.getTotalPrice());
        assertEquals("PENDING", response.getPaymentStatus());
    }

    /**
     * Teste la création d'une réservation quando l'utilisateur n'existe pas.
     * Doit lever une exception BusinessException.
     */
    @Test
    void createBooking_UserNotFound_ThrowsException() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(1L);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> bookingService.createBooking(request, "test@example.com"));
    }

    /**
     * Teste la création d'une réservation quando l'événement n'existe pas.
     * Doit lever une exception BusinessException.
     */
    @Test
    void createBooking_EventNotFound_ThrowsException() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(1L);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> bookingService.createBooking(request, "test@example.com"));
    }

    /**
     * Teste la création d'une réservation duplicate par le même utilisateur.
     * Doit lever une exception BusinessException.
     */
    @Test
    void createBooking_AlreadyBooked_ThrowsException() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(1L);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(testEvent));
        when(bookingRepository.existsByEventIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> bookingService.createBooking(request, "test@example.com"));
    }

    /**
     * Teste la création d'une réservation quand il n'y a plus de places disponibles.
     * Doit lever une exception BusinessException.
     */
    @Test
    void createBooking_NoSeatsAvailable_ThrowsException() {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(1L);
        testEvent.setAvailableSeats(0);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(eventRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(testEvent));
        when(bookingRepository.existsByEventIdAndUserId(1L, 1L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> bookingService.createBooking(request, "test@example.com"));
    }

    /**
     * Teste la récupération des réservations d'un utilisateur avec succès.
     * Vérifie que la liste contient la réservation créée.
     */
    @Test
    void getMyBookings_Success() {
        testEvent.setOrganizer(testUser);
        Booking booking = Booking.builder()
                .id(1L)
                .event(testEvent)
                .user(testUser)
                .totalPrice(BigDecimal.valueOf(100))
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(bookingRepository.findByUser(testUser)).thenReturn(List.of(booking));

        List<BookingResponse> bookings = bookingService.getMyBookings("test@example.com");

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getBookingId());
    }

    /**
     * Teste la récupération des réservations quand l'utilisateur n'existe pas.
     * Doit lever une exception BusinessException.
     */
    @Test
    void getMyBookings_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> bookingService.getMyBookings("test@example.com"));
    }
}