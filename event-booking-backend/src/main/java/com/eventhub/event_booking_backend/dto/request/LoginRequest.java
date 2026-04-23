package com.eventhub.event_booking_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de requête pour la connexion d'un utilisateur.
 * Contient les identifiants nécessaires à l'authentification.
 */
@Data
public class LoginRequest {
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}