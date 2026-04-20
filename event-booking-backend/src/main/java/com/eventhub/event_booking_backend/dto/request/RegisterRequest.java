package com.eventhub.event_booking_backend.dto.request;

import com.eventhub.event_booking_backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}
