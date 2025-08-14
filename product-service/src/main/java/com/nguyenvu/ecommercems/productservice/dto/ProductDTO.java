package com.nguyenvu.ecommercems.productservice.dto;

import com.nguyenvu.ecommercems.productservice.model.embedded.*;
import com.nguyenvu.ecommercems.productservice.model.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    
    private String id;
    
    // ===== THÃ”NG TIN CÆ  Báº¢N =====
    @NotBlank(message = "Product code is required")
    private String code;                    // MÃ£ sÃ¡ch ná»™i bá»™ (BOOK2024001)

    @NotBlank(message = "ISBN is required")
    private String sku;                    // ISBN-13
    
    @NotBlank(message = "Product title is required")
    private String title;
    
    private String subtitle;
    private String description;
    
    // ===== THÃ”NG TIN XUáº¤T Báº¢N =====
    private Manufacturer Manufacturer;
    private Integer publishedYear;
    private LocalDate publishedDate;          // NgÃ y xuáº¥t báº£n (táº¥t cáº£ sÃ¡ch)
    private LocalDateTime releaseTime;        // Thá»i Ä‘iá»ƒm má»Ÿ bÃ¡n chÃ­nh xÃ¡c (chá»‰ sÃ¡ch cá»±c ká»³ hot)
    private String edition;
    
    // ===== THÃ”NG TIN Váº¬T LÃ =====
    private Physical physical;              // Format, language, pageCount, dimensions, weight
    
    // ===== TÃC GIáº¢ =====
    private List<Supplier> Suppliers;
    
    // ===== PHÃ‚N LOáº I =====
    private List<ProductCategory> categories;
    private List<String> subjects;
    private AgeGroup ageGroup;              // enum: CHILDREN, TEEN, ADULT, ALL_AGES
    
    // ===== GIÃ Cáº¢ =====
    private Pricing pricing;
    
    // ===== HÃŒNH áº¢NH =====
    private Images images;
    
    // ===== ÄÃNH GIÃ CHI TIáº¾T =====
    private Rating rating;
    
    // ===== TRáº NG THÃI & KHO =====
    private ProductStatus status;              // enum: ACTIVE, INACTIVE, DISCONTINUED, PRE_ORDER
    private Availability availability;      // enum: IN_STOCK, OUT_OF_STOCK, PRE_ORDER, LIMITED
    
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;
    
    private Integer reservedQuantity;
    private Integer availableQuantity;
    
    // ===== BUSINESS LOGIC =====
    private Featured featured;
    private Sales sales;
    
    // ===== SERIES INFORMATION =====
    private String seriesId;               // Reference to ProductSeries
    private String seriesName;             // Cached for display
    private Integer seriesVolume;
    private Integer totalVolumes;
    
    // ===== SEO =====
    private Seo seo;
    private List<String> tags;
    
    // ===== AUDIT =====
    private Audit audit;
    
    // ===== TIMESTAMPS =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

