package com.nguyenvu.ecommercems.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private String id;
    private String bookId;
    private String userId;
    private String title;
    private String content;
    private Double rating;
    private Boolean recommended;
    private Boolean verifiedPurchase;
    private Integer helpfulCount;
    private Integer reportCount;
    private String userName;
}
