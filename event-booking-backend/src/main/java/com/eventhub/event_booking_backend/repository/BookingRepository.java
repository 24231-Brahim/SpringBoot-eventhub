package com.eventhub.event_booking_backend.repository;

import com.eventhub.event_booking_backend.model.Booking;
import com.eventhub.event_booking_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
    * Repository JPA pour l'entité Booking.
    * Fournit les opérations CRUD de base ainsi que des requêtes personnalisées.
*/
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
        * Recherche toutes les réservations d'un utilisateur.
        * @param user L'utilisateur dont on veut récupérer les réservations.
        * @return Une liste de réservations de cet utilisateur.
    */
    List<Booking> findByUser(User user);

    /**
        * Vérifie si un utilisateur a déjà réservé un événement spécifique.
        * @param eventId L'ID de l'événement.
        * @param userId L'ID de l'utilisateur.
        * @return true si une réservation existe, false sinon.
    */
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}