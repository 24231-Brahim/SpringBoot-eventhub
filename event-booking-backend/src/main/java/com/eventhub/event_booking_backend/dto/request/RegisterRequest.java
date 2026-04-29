package com.eventhub.event_booking_backend.dto.request;

import com.eventhub.event_booking_backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de requête pour l'inscription d'un nouvel utilisateur.
 * Contient les informations nécessaires à la création du compte.
 */
@Data
public class RegisterRequest {
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}
