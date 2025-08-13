package com.nguyenvu.ecommercems.productservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double rating;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 5000)
    private String content;

    @Size(max = 10)
    private List<String> pros = new ArrayList<>();

    @Size(max = 10)
    private List<String> cons = new ArrayList<>();

    private Boolean recommended;

    private String userId;
    private String userName;
    private Boolean verifiedPurchase;
}
