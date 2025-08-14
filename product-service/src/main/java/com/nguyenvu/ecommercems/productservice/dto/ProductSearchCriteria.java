package com.nguyenvu.ecommercems.productservice.dto;

import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductType;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchCriteria {
    
    // ===== TEXT SEARCH =====
    private String searchText;              // Full-text search in name, description, keywords
    private String nameContains;            // Name contains specific text
    private String descriptionContains;     // Description contains specific text
    
    // ===== CATEGORY FILTERS =====
    private List<String> categoryIds;       // Multiple category IDs
    private String categoryPath;            // Category hierarchy path
    private String categoryName;            // Category name search
    
    // ===== MANUFACTURER FILTERS =====
    private String manufacturerId;          // Manufacturer ID (exact match)
    private String manufacturerName;        // Manufacturer name (partial match)
    private List<String> manufacturerIds;   // Multiple manufacturer IDs
    
    // ===== SUPPLIER FILTERS =====
    private String supplierId;              // Supplier ID (exact match)
    private String supplierName;            // Supplier name (partial match)
    private List<String> supplierIds;       // Multiple supplier IDs
    
    // ===== PRICE FILTERS =====
    private BigDecimal minPrice;            // Minimum sale price
    private BigDecimal maxPrice;            // Maximum sale price
    private Boolean hasDiscount;            // Only discounted products
    private Double minDiscountPercentage;   // Minimum discount percentage
    
    // ===== RATING FILTERS =====
    private Double minRating;               // Minimum average rating
    private Integer minReviewCount;         // Minimum number of reviews
    
    // ===== AVAILABILITY & STOCK FILTERS =====
    private List<Availability> availabilities;  // Multiple availability statuses
    private Boolean inStock;                // Only in-stock products
    private Integer minStock;               // Minimum stock quantity
    
    // ===== PRODUCT TYPE & CONDITION =====
    private List<ProductType> productTypes;    // Multiple product types (PHYSICAL, DIGITAL, SERVICE)
    private List<ProductCondition> conditions; // Multiple conditions (NEW, USED, REFURBISHED)
    
    // ===== PHYSICAL PROPERTIES =====
    private List<String> colors;           // Multiple colors
    private List<String> sizes;            // Multiple sizes  
    private List<String> materials;        // Multiple materials
    private Double minWeight;               // Minimum weight (kg)
    private Double maxWeight;               // Maximum weight (kg)
    
    // ===== SERIES & VERSION =====
    private String seriesId;                // Products in specific series
    private String seriesName;              // Series name search
    private String version;                 // Specific version
    
    // ===== FEATURED & STATUS =====
    private Boolean isFeatured;             // Only featured products
    private Boolean isNewArrival;           // Only new arrivals
    private Boolean isBestseller;           // Only bestsellers
    private Boolean isOnSale;               // Only products on sale
    private Boolean isActive;               // Only active products
    
    // ===== DATE FILTERS =====
    private Integer releaseYear;            // Released in specific year
    private Integer releaseYearFrom;        // Released from year
    private Integer releaseYearTo;          // Released to year
    private LocalDateTime createdAfter;     // Created after date
    private LocalDateTime createdBefore;    // Created before date
    
    // ===== CONTENT & IDENTIFICATION =====
    private List<String> keywords;          // Product keywords
    private List<String> tags;              // Product tags
    private String sku;                     // SKU search
    private String barcode;                 // Barcode search
    private String model;                   // Product model
    
    // ===== SALES FILTERS =====
    private Integer minTotalSold;           // Minimum total sold
    private BigDecimal minSalesRevenue;     // Minimum sales revenue
    
    // ===== ADVANCED FILTERS =====
    private Boolean hasImages;              // Only products with images
    private Boolean hasVideo;               // Only products with video
    private Boolean hasReviews;             // Only products with reviews
    private String targetAudience;          // Target audience
    private String ageGroup;                // Target age group
    private Boolean freeShipping;          // Only products with free shipping
    private Integer minWarrantyMonths;      // Minimum warranty period
    
    // ===== SORTING & PAGINATION =====
    private String sortBy;                  // Sort field (name, price, rating, created, sales, stock)
    private String sortDirection;           // Sort direction (asc, desc)
    private List<String> multiSort;         // Multiple sort criteria: ["price:asc", "rating:desc"]
    
    // ===== PERFORMANCE & OUTPUT =====
    private Boolean includeTotalCount;      // Whether to include total count
    private List<String> includeFields;    // Only return specific fields (projection)
    private List<String> excludeFields;    // Exclude specific fields
    private Integer maxResults;             // Maximum number of results
    
    // ===== HELPER METHODS =====
    
    /**
     * Check if any price filter is applied
     */
    public boolean hasPriceFilter() {
        return minPrice != null || maxPrice != null || hasDiscount != null || minDiscountPercentage != null;
    }
    
    /**
     * Check if any date filter is applied
     */
    public boolean hasDateFilter() {
        return releaseYear != null || releaseYearFrom != null || releaseYearTo != null 
                || createdAfter != null || createdBefore != null;
    }
    
    /**
     * Check if any stock filter is applied
     */
    public boolean hasStockFilter() {
        return availabilities != null || inStock != null || minStock != null;
    }
    
    /**
     * Check if search is for physical products only
     */
    public boolean isPhysicalProductsOnly() {
        return productTypes != null && productTypes.size() == 1 
                && productTypes.contains(ProductType.PHYSICAL);
    }
    
    /**
     * Check if search is for digital products only
     */
    public boolean isDigitalProductsOnly() {
        return productTypes != null && productTypes.size() == 1 
                && productTypes.contains(ProductType.DIGITAL);
    }
}
