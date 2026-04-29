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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service de gestion des réservations.
 * Gère la création de réservations et la récupération des réservations utilisateur.
 */
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * Crée une nouvelle réservation pour un événement.
     * Utilise un verrou pessimiste pour éviter les sur-réservations.
     * @param request L'ID de l'événement à réserver.
     * @param email L'email de l'utilisateur réservant.
     * @return Les détails de la réservation créée.
     * @throws BusinessException si l'utilisateur n'existe pas, l'événement n'existe pas,
     *         l'utilisateur a déjà réservé, ou plus de places disponibles.
     */
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        Event event = eventRepository.findByIdForUpdate(request.getEventId())
                .orElseThrow(() -> new BusinessException("Event not found"));

        if (bookingRepository.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new BusinessException("You already booked this event");
        }

        if (event.getAvailableSeats() <= 0) {
            throw new BusinessException("No seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() - 1);

        Booking booking = Booking.builder()
                .event(event)
                .user(user)
                .totalPrice(event.getPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    /**
     * Récupère toutes les réservations d'un utilisateur.
     * @param email L'email de l'utilisateur.
     * @return Une liste des réservations de l'utilisateur.
     * @throws BusinessException si l'utilisateur n'existe pas.
     */
    public List<BookingResponse> getMyBookings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return bookingRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Convertit une entité Booking en BookingResponse.
     * @param booking L'entité à convertir.
     * @return Le DTO de réponse.
     */
    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .eventId(booking.getEvent().getId())
                .eventTitle(booking.getEvent().getTitle())
                .totalPrice(booking.getTotalPrice())
                .paymentStatus(booking.getPaymentStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}