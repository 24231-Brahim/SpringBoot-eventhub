package com.eventhub.event_booking_backend.config;

import com.eventhub.event_booking_backend.model.Role;
import com.eventhub.event_booking_backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Configuration
public class TestConfig {

    @Autowired
    private JwtService jwtService;

    /**
     * Génère un token JWT pour un utilisateur avec un rôle spécifique
     * @param email L'email de l'utilisateur
     * @param role Le rôle de l'utilisateur
     * @return Le token JWT généré
     */
    public String generateToken(String email, Role role) {
        UserDetails userDetails = User.builder()
                .username(email)
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority(role.name())))
                .build();
        return jwtService.generateToken(userDetails);
    }

    /**
     * Génère un token JWT pour un utilisateur standard
     * @param email L'email de l'utilisateur
     * @return Le token JWT généré
     */
    public String generateUserToken(String email) {
        return generateToken(email, Role.ROLE_USER);
    }

    /**
     * Génère un token JWT pour un organisateur
     * @param email L'email de l'organisateur
     * @return Le token JWT généré
     */
    public String generateOrganizerToken(String email) {
        return generateToken(email, Role.ROLE_ORGANIZER);
    }

    /**
     * Génère un token JWT pour un administrateur
     * @param email L'email de l'administrateur
     * @return Le token JWT généré
     */
    public String generateAdminToken(String email) {
        return generateToken(email, Role.ROLE_ADMIN);
    }
}
