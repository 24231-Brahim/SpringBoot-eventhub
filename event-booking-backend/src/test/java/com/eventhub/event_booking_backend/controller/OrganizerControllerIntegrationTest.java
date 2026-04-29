package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.config.TestConfig;
import com.eventhub.event_booking_backend.dto.request.RegisterRequest;
import com.eventhub.event_booking_backend.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrganizerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestConfig testConfig;

    private String organizerToken;
    private String organizerEmail;

    @BeforeEach
    void setUp() throws Exception {
        // Inscription d'un organisateur pour les tests
        organizerEmail = "organizer2_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(organizerEmail);
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.ROLE_ORGANIZER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        organizerToken = testConfig.generateOrganizerToken(organizerEmail);
    }

    @Test
    void shouldCreateEvent() throws Exception {
        // Test de création d'un événement par un organisateur
        String eventJson = """
                {
                    "title": "Integration Test Event",
                    "description": "Test Description",
                    "category": "TECH",
                    "startDate": "2026-06-01T10:00:00",
                    "endDate": "2026-06-01T18:00:00",
                    "capacity": 200,
                    "price": 75.00
                }
                """;

        mockMvc.perform(post("/api/v1/organizer/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test Event"))
                .andExpect(jsonPath("$.category").value("TECH"))
                .andExpect(jsonPath("$.capacity").value(200));
    }

    @Test
    void shouldNotCreateEventWithoutAuth() throws Exception {
        // Test de création d'événement sans authentification
        String eventJson = """
                {
                    "title": "Unauthorized Event",
                    "description": "Test Description",
                    "category": "TECH",
                    "startDate": "2026-06-01T10:00:00",
                    "endDate": "2026-06-01T18:00:00",
                    "capacity": 200,
                    "price": 75.00
                }
                """;

        mockMvc.perform(post("/api/v1/organizer/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotCreateEventWithInvalidData() throws Exception {
        // Test de création d'événement avec des données invalides
        String eventJson = """
                {
                    "title": "",
                    "description": "Test Description",
                    "category": "INVALID_CATEGORY",
                    "startDate": "2026-06-01T10:00:00",
                    "endDate": "2026-05-01T18:00:00",
                    "capacity": -10,
                    "price": -5.00
                }
                """;

        mockMvc.perform(post("/api/v1/organizer/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateEventWithUserRole() throws Exception {
        // Test de création d'événement par un utilisateur simple (rôle insuffisant)
        String userEmail = "regularuser_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest userRequest = new RegisterRequest();
        userRequest.setEmail(userEmail);
        userRequest.setPassword("password123");
        userRequest.setRole(Role.ROLE_USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        String userToken = testConfig.generateUserToken(userEmail);

        String eventJson = """
                {
                    "title": "User Event",
                    "description": "Test Description",
                    "category": "TECH",
                    "startDate": "2026-06-01T10:00:00",
                    "endDate": "2026-06-01T18:00:00",
                    "capacity": 200,
                    "price": 75.00
                }
                """;

        mockMvc.perform(post("/api/v1/organizer/events")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isForbidden());
    }
}
