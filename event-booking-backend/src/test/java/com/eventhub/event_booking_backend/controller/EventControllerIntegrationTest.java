package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.config.TestConfig;
import com.eventhub.event_booking_backend.dto.request.RegisterRequest;
import com.eventhub.event_booking_backend.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestConfig testConfig;

    private String organizerToken;
    private Long eventId;

    @BeforeEach
    void setUp() throws Exception {
        // Inscription d'un organisateur pour les tests
        String email = "organizer_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.ROLE_ORGANIZER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        organizerToken = testConfig.generateOrganizerToken(email);

        eventId = createEvent(organizerToken);
    }

    @Test
    void shouldGetAllEvents() throws Exception {
        // Test de récupération de tous les événements
        mockMvc.perform(get("/api/v1/public/events"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllEventsByCategory() throws Exception {
        // Test de récupération des événements filtrés par catégorie
        mockMvc.perform(get("/api/v1/public/events")
                        .param("category", "MUSIC"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetEventById() throws Exception {
        // Test de récupération d'un événement par son ID
        mockMvc.perform(get("/api/v1/public/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Event"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentEvent() throws Exception {
        // Test de récupération d'un événement inexistant
        mockMvc.perform(get("/api/v1/public/events/99999"))
                .andExpect(status().isBadRequest());
    }

    private Long createEvent(String token) throws Exception {
        // Création d'un événement pour les tests
        String eventJson = """
                {
                    "title": "Test Event",
                    "description": "Test Description",
                    "category": "MUSIC",
                    "startDate": "2026-05-01T10:00:00",
                    "endDate": "2026-05-01T18:00:00",
                    "capacity": 100,
                    "price": 50.00
                }
                """;

        String response = mockMvc.perform(post("/api/v1/organizer/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("id").asLong();
    }
}
