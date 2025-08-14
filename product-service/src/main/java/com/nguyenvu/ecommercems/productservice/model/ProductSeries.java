package com.nguyenvu.ecommercems.productservice.model;

import com.nguyenvu.ecommercems.productservice.model.embedded.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "product_series")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSeries {
    @Id
    private String id;
    
    private String name;
    
    @Indexed(unique = true)
    private String slug;
    
    private String description;
    private Manufacturer Manufacturer;
    
    // Series metadata
    private Integer totalProducts;
    private Integer publishedProducts;
    private Integer plannedProducts;
    private Boolean isCompleted;
    
    // Categorization
    private String primaryCategory;
    private List<ProductCategory> categories;
    private List<String> targetAudience;
    private String readingLevel;        // "beginner", "intermediate", "advanced"
    
    // Commercial info
    private Long totalSales;
    private Double avgRating;
    private Integer totalReviews;
    
    // Publication info
    private LocalDateTime firstPublished;
    private LocalDateTime lastPublished;
    private String publicationFrequency; // "monthly", "quarterly", "yearly"
    
    // SEO and marketing
    private Images images;              // Series cover, banner
    private List<String> tags;
    private Seo seo;
    
    // Status
    private String status;              // "active", "completed", "discontinued", "on_hold"
    private Boolean featured;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

