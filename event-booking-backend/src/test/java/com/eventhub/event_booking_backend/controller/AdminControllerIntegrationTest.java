package com.eventhub.event_booking_backend.controller;

import com.eventhub.event_booking_backend.config.TestConfig;
import com.eventhub.event_booking_backend.model.Role;
import com.eventhub.event_booking_backend.model.User;
import com.eventhub.event_booking_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setUp() {
        String adminEmail = "admin_direct_" + System.currentTimeMillis() + "@example.com";
        User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(admin);
        adminToken = testConfig.generateAdminToken(adminEmail);
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldNotAccessAdminWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAccessAdminWithUserRole() throws Exception {
        String userEmail = "regular_" + System.currentTimeMillis() + "@example.com";
        User regularUser = User.builder()
                .email(userEmail)
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(regularUser);
        String userToken = testConfig.generateUserToken(userEmail);

        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetAllEvents() throws Exception {
        mockMvc.perform(get("/api/v1/admin/events")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
