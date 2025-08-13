package com.nguyenvu.ecommercems.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(03|05|07|08|09)[0-9]{8}$",
        message = "Phone number must be a valid Vietnamese phone number (10 digits starting with 03, 05, 07, 08, or 09)"
    )
    private String phoneNumber;
    
    @NotBlank(message = "Password is required")
    private String password;
}
