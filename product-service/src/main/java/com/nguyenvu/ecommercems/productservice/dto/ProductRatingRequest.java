package com.nguyenvu.ecommercems.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRatingRequest {
    @NotNull(message = "Rating is required")
    @DecimalMax(value = "5.0", message = "Rating must be between 1 and 5")
    @DecimalMin(value = "1.0", message = "Rating must be between 1 and 5")
    private Double rating;

    @Size(max = 1000, message = "Review cannot exceed 1000 characters")
    private String review;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Size(max = 100, message = "Username cannot exceed 100 characters")
    private String username;

    private Boolean verified;
}
