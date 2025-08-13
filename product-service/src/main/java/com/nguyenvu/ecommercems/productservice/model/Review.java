package com.nguyenvu.ecommercems.productservice.model;

import com.nguyenvu.ecommercems.productservice.model.enums.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@Schema(description = "Product review document")
public class Review {
    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Product ID cannot be blank")
    @Schema(description = "ID of the Product being reviewed", example = "BOOK0000001")
    private String bookId;

    @Indexed
    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content;

    @Size(max = 10)
    private List<String> pros = new ArrayList<>();

    @Size(max = 10)
    private List<String> cons = new ArrayList<>();

    private Boolean recommended;
    private Boolean verifiedPurchase;

    @Builder.Default
    private Integer helpfulCount = 0;

    @Builder.Default
    private Integer reportCount = 0;

    @Builder.Default
    private ReviewStatus status = ReviewStatus.APPROVED;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedDate
    private LocalDateTime updatedAt;


    // Denormalized data
    private String bookTitle;
    private String bookISBN;
    private String username;
    private String userAvatar;

}
