package com.nguyenvu.ecommercems.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewDTO {
    private String reviewId;
    private String bookId;
    private String userId;
    private String username;
    private Double rating;
    private String comment;
    private Boolean verified;
    private LocalDateTime createdAt;
    private Integer helpfulCount;
}
