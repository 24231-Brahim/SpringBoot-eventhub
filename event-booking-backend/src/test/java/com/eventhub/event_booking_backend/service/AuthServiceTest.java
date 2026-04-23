package com.eventhub.event_booking_backend.service;

import com.eventhub.event_booking_backend.dto.request.LoginRequest;
import com.eventhub.event_booking_backend.dto.request.RegisterRequest;
import com.eventhub.event_booking_backend.dto.response.AuthResponse;
import com.eventhub.event_booking_backend.exception.BusinessException;
import com.eventhub.event_booking_backend.model.Role;
import com.eventhub.event_booking_backend.model.User;
import com.eventhub.event_booking_backend.repository.UserRepository;
import com.eventhub.event_booking_backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link AuthService}.
 * Vérifie les fonctionnalités d'inscription et de connexion.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    /**
     * Initialise les données de test avant chaque test.
     */
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    /**
     * Teste l'inscription d'un nouvel utilisateur avec succès.
     * Vérifie que le token JWT est généré et les informations sont retournées.
     */
    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(Role.ROLE_USER);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());
    }

    /**
     * Teste l'inscription quand l'email existe déjà.
     * Doit lever une exception BusinessException.
     */
    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(Role.ROLE_USER);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(request));
    }

    /**
     * Teste la connexion d'un utilisateur existant avec succès.
     * Vérifie que le token JWT est généré.
     */
    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("test@example.com", response.getEmail());
    }
}