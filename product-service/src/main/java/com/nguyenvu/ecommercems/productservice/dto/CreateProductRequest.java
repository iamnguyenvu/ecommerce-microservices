package com.nguyenvu.ecommercems.productservice.dto;

import com.nguyenvu.ecommercems.productservice.model.embedded.*;
import com.nguyenvu.ecommercems.productservice.model.enums.*;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    
    // ===== BASIC INFORMATION =====
    @NotBlank(message = "Product code is required")
    @Size(max = 50, message = "Product code must not exceed 50 characters")
    private String code;                    // Internal product code (PROD2024001)

    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;                     // Stock Keeping Unit (LAPTOP-001)
    
    @NotNull(message = "Product type is required")
    private ProductType type;               // ELECTRONICS, CLOTHING, etc.
    
    @NotBlank(message = "Product title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 255, message = "Subtitle must not exceed 255 characters")
    private String subtitle;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    // ===== MANUFACTURING INFORMATION =====
    private ManufacturerRequest manufacturer;
    
    @Min(value = 1900, message = "Manufactured year must be after 1900")
    @Max(value = 2030, message = "Manufactured year must be before 2030")
    private Integer manufacturedYear;
    
    private LocalDate releaseDate;          // Product release date
    private LocalDateTime launchTime;       // Exact launch time for special products
    
    @Size(max = 100, message = "Version must not exceed 100 characters")
    private String version;                 // Product version (v1.0, 2024 Edition)
    
    // ===== PHYSICAL PROPERTIES =====
    private PhysicalRequest physical;
    
    // ===== SUPPLIERS =====
    @Valid
    private List<SupplierRequest> suppliers;
    
    // ===== CATEGORIES =====
    @NotEmpty(message = "At least one category is required")
    @Valid
    private List<ProductCategoryRequest> categories;
    
    private List<String> subjects;          // Additional categorization tags
    private AgeGroup ageGroup;              // CHILDREN, TEEN, ADULT, ALL_AGES
    
    // ===== PRICING =====
    @NotNull(message = "Pricing information is required")
    @Valid
    private PricingRequest pricing;
    
    // ===== IMAGES =====
    @Valid
    private ImagesRequest images;
    
    // ===== INVENTORY =====
    @PositiveOrZero(message = "Initial stock quantity must be zero or positive")
    private Integer initialStockQuantity = 0;
    
    @PositiveOrZero(message = "Low stock threshold must be zero or positive")
    private Integer lowStockThreshold = 5;
    
    // ===== PRODUCT SERIES =====
    private String seriesId;               // Reference to existing ProductSeries
    private String seriesName;             // For creating new series
    private Integer seriesVolume;          // Volume/part number in series
    
    // ===== SEO & MARKETING =====
    @Valid
    private SeoRequest seo;
    
    @Size(max = 20, message = "Maximum 20 tags allowed")
    private List<@Size(max = 50, message = "Each tag must not exceed 50 characters") String> tags;
    
    // ===== BUSINESS FEATURES =====
    private Boolean isFeatured = false;     // Whether product should be featured
    private Boolean isActive = true;        // Whether product should be active initially
    
    // Nested DTOs for complex fields
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ManufacturerRequest {
        @NotBlank(message = "Manufacturer name is required")
        private String name;
        
        @Email(message = "Valid email is required")
        private String email;
        
        private String website;
        private String description;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhysicalRequest {
        private String format;              // LAPTOP, SMARTPHONE, BOOK, etc.
        private String language;
        private DimensionsRequest dimensions;
        private Double weight;              // in kg
        private String color;
        private String material;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DimensionsRequest {
        private Double length;              // in cm
        private Double width;               // in cm
        private Double height;              // in cm
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SupplierRequest {
        @NotBlank(message = "Supplier name is required")
        private String name;
        
        @NotNull(message = "Supplier role is required")
        private SupplierRole role;          // PRIMARY, SECONDARY, BACKUP
        
        @Email(message = "Valid email is required")
        private String email;
        
        private String website;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductCategoryRequest {
        @NotBlank(message = "Category name is required")
        private String name;
        
        private String parentId;            // For hierarchical categories
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PricingRequest {
        @NotNull(message = "Sale price is required")
        @Positive(message = "Sale price must be positive")
        private Double salePrice;
        
        @Positive(message = "List price must be positive")
        private Double listPrice;           // Original price before discount
        
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        private String currency = "USD";
        
        private Double discountPercentage;  // Calculated discount
        private LocalDate saleStartDate;    // When sale price becomes active
        private LocalDate saleEndDate;      // When sale price ends
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImagesRequest {
        @org.hibernate.validator.constraints.URL(message = "Thumbnail must be a valid URL")
        private String thumbnail;
        
        @org.hibernate.validator.constraints.URL(message = "Main image must be a valid URL")
        private String main;
        
        private List<@org.hibernate.validator.constraints.URL(message = "Each gallery image must be a valid URL") String> gallery;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeoRequest {
        @Size(max = 160, message = "Meta title must not exceed 160 characters")
        private String metaTitle;
        
        @Size(max = 320, message = "Meta description must not exceed 320 characters")
        private String metaDescription;
        
        private List<@Size(max = 50, message = "Each keyword must not exceed 50 characters") String> keywords;
        
        @Size(max = 100, message = "URL slug must not exceed 100 characters")
        private String urlSlug;            // For SEO-friendly URLs
    }
}
