package com.eventhub.event_booking_backend.repository;

import com.eventhub.event_booking_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository JPA pour l'entité User.
 * Fournit les opérations CRUD de base ainsi que des requêtes personnalisées par email.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse email.
     * @param email L'adresse email de l'utilisateur.
     * @return Un Optional contenant l'utilisateur si trouvé, vide sinon.
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un utilisateur existe déjà avec cet email.
     * @param email L'adresse email à vérifier.
     * @return true si un utilisateur avec cet email existe, false sinon.
     */
    boolean existsByEmail(String email);
}