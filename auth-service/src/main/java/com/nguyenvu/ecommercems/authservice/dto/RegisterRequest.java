package com.nguyenvu.ecommercems.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(03|05|07|08|09)[0-9]{8}$",
        message = "Phone number must be a valid Vietnamese phone number (10 digits starting with 03, 05, 07, 08, or 09)"
    )
    private String phoneNumber;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters and at most 20 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character, and be between 8 and 20 characters long"
    )
    private String password;
}
