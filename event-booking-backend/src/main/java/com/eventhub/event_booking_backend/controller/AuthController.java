package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.dto.request.LoginRequest;
import com.eventhub.event_booking_backend.dto.request.RegisterRequest;
import com.eventhub.event_booking_backend.dto.response.AuthResponse;
import com.eventhub.event_booking_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification.
 * Gère les endpoints publics d'inscription et de connexion.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint d'inscription d'un nouvel utilisateur.
     * @param request Les données d'inscription (email, mot de passe, rôle).
     * @return Le token JWT et les informations utilisateur.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Endpoint de connexion d'un utilisateur existant.
     * @param request Les identifiants (email, mot de passe).
     * @return Le token JWT et les informations utilisateur.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}