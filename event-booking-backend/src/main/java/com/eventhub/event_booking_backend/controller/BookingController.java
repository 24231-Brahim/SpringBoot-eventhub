package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.dto.request.BookingCreateRequest;
import com.eventhub.event_booking_backend.dto.response.BookingResponse;
import com.eventhub.event_booking_backend.service.BookingService;
import com.eventhub.event_booking_backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Contrôleur REST pour les réservations utilisateur.
 * Gère la création de réservations et la consultation des réservations personnelles.
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    /**
     * Crée une nouvelle réservation.
     * L'utilisateur est automatiquement récupéré depuis le token JWT.
     * @param request L'ID de l'événement à réserver.
     * @param authentication L'authentification contenant l'email de l'utilisateur.
     * @return Les détails de la réservation créée.
     */
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingCreateRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(bookingService.createBooking(request, authentication.getName()));
    }

    /**
     * Récupère toutes les réservations de l'utilisateur connecté.
     * @param authentication L'authentification contenant l'email de l'utilisateur.
     * @return Une liste des réservations de l'utilisateur.
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getMyBookings(authentication.getName()));
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId, Authentication authentication) {
        bookingService.cancelBooking(bookingId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bookings/{bookingId}/pay")
    public ResponseEntity<Void> payBooking(@PathVariable Long bookingId, Authentication authentication) {
        paymentService.processPayment(bookingId);
        return ResponseEntity.ok().build();
    }
}