package com.eventhub.event_booking_backend.repository;

import com.eventhub.event_booking_backend.model.Category;
import com.eventhub.event_booking_backend.model.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
    * Repository JPA pour l'entité Event.
    * Fournit les opérations CRUD de base ainsi que des requêtes personnalisées par catégorie et avec verrouillage pessimiste.
*/
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
        * Recherche tous les événements d'une catégorie spécifique.
        * @param category La catégorie d'événements à rechercher.
        * @return Une liste d'événements de cette catégorie.
    */
    List<Event> findByCategory(Category category);

    /**
        * Recherche un événement par ID avec verrouillage pessimiste.
        * Empêche les conditions de course lors de la réservation de places.
        * @param id L'ID de l'événement.
        * @return Un Optional contenant l'événement avec verrou si trouvé.
    */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);
}