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
    
    // ===== BASIC INFORMATION =====
    @NotBlank(message = "SKU is required")
    private String sku;                     // Stock Keeping Unit (LAPTOP-001)
    
    @NotNull(message = "Product type is required")
    private ProductType type;               // ELECTRONICS, CLOTHING, etc.
    
    @NotBlank(message = "Product title is required")
    private String title;
    
    private String subtitle;
    private String description;
    
    // ===== MANUFACTURING INFORMATION =====
    private Manufacturer manufacturer;      // Changed from Publisher
    private Integer manufacturedYear;       // Changed from publishedYear
    private LocalDate releaseDate;          // Product release date
    private LocalDateTime launchTime;       // Exact launch time for special products
    private String version;                 // Changed from edition (v1.0, 2024 Edition)
    
    // ===== PHYSICAL PROPERTIES =====
    private Physical physical;              // Format, language, dimensions, weight
    
    // ===== SUPPLIERS =====
    private List<Supplier> suppliers;       // Product suppliers/distributors
    
    // ===== CATEGORIES =====
    private List<ProductCategory> categories;
    private List<String> subjects;          // Additional categorization
    private AgeGroup ageGroup;              // CHILDREN, TEEN, ADULT, ALL_AGES
    
    // ===== PRICING =====
    @NotNull(message = "Pricing information is required")
    private Pricing pricing;
    
    // ===== IMAGES =====
    private Images images;
    
    // ===== RATING & REVIEWS =====
    private Rating rating;
    
    // ===== INVENTORY & AVAILABILITY =====
    @NotNull(message = "Product status is required")
    private ProductStatus status;           // ACTIVE, INACTIVE, DISCONTINUED, PRE_ORDER
    
    @NotNull(message = "Availability status is required")
    private Availability availability;      // IN_STOCK, OUT_OF_STOCK, PRE_ORDER, LIMITED
    
    @PositiveOrZero(message = "Stock quantity must be zero or positive")
    private Integer stockQuantity;
    
    @PositiveOrZero(message = "Reserved quantity must be zero or positive")
    private Integer reservedQuantity;
    
    private Integer availableQuantity;      // Calculated: stockQuantity - reservedQuantity
    
    // ===== BUSINESS FEATURES =====
    private Featured featured;              // Featured product information
    private Sales sales;                    // Sales metrics and statistics
    
    // ===== PRODUCT SERIES INFORMATION =====
    private String seriesId;               // Reference to ProductSeries
    private String seriesName;             // Cached for display
    private Integer seriesVolume;          // Volume/part number in series
    private Integer totalVolumes;          // Total volumes in series
    
    // ===== SEO & MARKETING =====
    private Seo seo;                       // SEO metadata
    private List<String> tags;             // Marketing tags
    
    // ===== AUDIT INFORMATION =====
    private Audit audit;                   // Audit trail information
    
    // ===== TIMESTAMPS =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
