package com.eventhub.event_booking_backend.service;

import com.eventhub.event_booking_backend.dto.request.LoginRequest;
import com.eventhub.event_booking_backend.dto.request.RegisterRequest;
import com.eventhub.event_booking_backend.dto.response.AuthResponse;
import com.eventhub.event_booking_backend.exception.BusinessException;
import com.eventhub.event_booking_backend.repository.UserRepository;
import com.eventhub.event_booking_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service de gestion de l'authentification.
 * Gère l'inscription, la connexion et la génération de tokens JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Inscrit un nouvel utilisateur dans le système.
     * Encode le mot de passe, sauvegarde l'utilisateur et génère un token JWT.
     * @param request Les données d'inscription (email, mot de passe, rôle).
     * @return Une réponse contenant le token JWT, l'email et le rôle.
     * @throws BusinessException si l'email existe déjà.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        if (request.getRole() == com.eventhub.event_booking_backend.model.Role.ROLE_ADMIN) {
            throw new BusinessException("Admin registration is not allowed");
        }

        var savedUser = userRepository.save(
                com.eventhub.event_booking_backend.model.User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(request.getRole())
                        .build()
        );

        UserDetails userDetails = User.withUsername(savedUser.getEmail())
                .password(savedUser.getPassword())
                .roles(savedUser.getRole().name().replace("ROLE_", ""))
                .build();

        String token = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    /**
     * Authentifie un utilisateur existant.
     * Valide les identifiants via AuthenticationManager et génère un token JWT.
     * @param request Les identifiants (email, mot de passe).
     * @return Une réponse contenant le token JWT, l'email et le rôle.
     * @throws BusinessException si les identifiants sont invalides.
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        UserDetails userDetails = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name().replace("ROLE_", ""))
                .build();

        String token = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}