package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.config.TestConfig;
import com.eventhub.event_booking_backend.dto.request.BookingCreateRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestConfig testConfig;

    private String userToken;
    private String userEmail;
    private String organizerToken;
    private Long eventId;

    @BeforeEach
    void setUp() throws Exception {
        // Inscription d'un utilisateur pour les tests
        userEmail = "bookinguser_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest userRequest = new RegisterRequest();
        userRequest.setEmail(userEmail);
        userRequest.setPassword("password123");
        userRequest.setRole(Role.ROLE_USER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        userToken = testConfig.generateUserToken(userEmail);

        // Inscription d'un organisateur et création d'un événement
        String organizerEmail = "bookingorganizer_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest organizerRequest = new RegisterRequest();
        organizerRequest.setEmail(organizerEmail);
        organizerRequest.setPassword("password123");
        organizerRequest.setRole(Role.ROLE_ORGANIZER);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizerRequest)))
                .andExpect(status().isOk());

        organizerToken = testConfig.generateOrganizerToken(organizerEmail);

        eventId = createEvent(organizerToken);
    }

    @Test
    void shouldCreateBooking() throws Exception {
        // Test de création d'une réservation
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(eventId);

        mockMvc.perform(post("/api/v1/user/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.eventTitle").value("Booking Test Event"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    void shouldNotCreateBookingWithoutAuth() throws Exception {
        // Test de création de réservation sans authentification
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(eventId);

        mockMvc.perform(post("/api/v1/user/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotCreateDuplicateBooking() throws Exception {
        // Test de création d'une réservation en double
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(eventId);

        mockMvc.perform(post("/api/v1/user/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/user/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetMyBookings() throws Exception {
        // Test de récupération des réservations de l'utilisateur
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(eventId);

        mockMvc.perform(post("/api/v1/user/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/user/my-bookings")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].eventId").value(eventId));
    }

    @Test
    void shouldCancelBooking() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(eventId);

        String bookingResponse = mockMvc.perform(post("/api/v1/user/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(bookingResponse);
        Long bookingId = jsonNode.get("bookingId").asLong();

        mockMvc.perform(post("/api/v1/user/bookings/" + bookingId + "/cancel")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPayBooking() throws Exception {
        BookingCreateRequest request = new BookingCreateRequest();
        request.setEventId(eventId);

        String bookingResponse = mockMvc.perform(post("/api/v1/user/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(bookingResponse);
        Long bookingId = jsonNode.get("bookingId").asLong();

        mockMvc.perform(post("/api/v1/user/bookings/" + bookingId + "/pay")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotGetBookingsWithoutAuth() throws Exception {
        // Test de récupération des réservations sans authentification
        mockMvc.perform(get("/api/v1/user/my-bookings"))
                .andExpect(status().isForbidden());
    }

    private Long createEvent(String token) throws Exception {
        // Création d'un événement pour les tests de réservation
        String eventJson = """
                {
                    "title": "Booking Test Event",
                    "description": "Event for booking tests",
                    "category": "SPORT",
                    "startDate": "2026-07-01T10:00:00",
                    "endDate": "2026-07-01T18:00:00",
                    "capacity": 50,
                    "price": 25.00
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
