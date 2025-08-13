package com.nguyenvu.ecommercems.productservice.dto;

import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import com.nguyenvu.ecommercems.productservice.model.enums.Format;
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
    private String searchText;              // Full-text search in title, description, Suppliers
    
    // ===== CATEGORY FILTERS =====
    private List<String> categoryIds;       // Multiple category IDs
    private String categoryPath;            // Category hierarchy path
    private String categoryName;            // Category name search
    
    // ===== Supplier FILTERS =====
    private String authorName;              // Single Supplier name (partial match)
    private List<String> authorNames;       // Multiple Supplier names (exact match)
    private List<String> SupplierRole;        // Supplier roles filter
    
    // ===== PUBLISHER FILTERS =====
    private String publisherId;             // Publisher ID (exact match)
    private String publisherName;           // Publisher name (partial match)
    
    // ===== PRICE FILTERS =====
    private BigDecimal minPrice;            // Minimum sale price
    private BigDecimal maxPrice;            // Maximum sale price
    private Boolean hasDiscount;            // Only discounted products
    private Double discountPercentage;      // Minimum discount percentage
    
    // ===== RATING FILTERS =====
    private Double minRating;               // Minimum average rating
    private Double maxRating;               // Maximum average rating
    private Integer minReviewCount;         // Minimum number of reviews
    
    // ===== AVAILABILITY FILTERS =====
    private Availability availability;       // Stock availability status (ENUM)
    private Boolean inStock;                // Only in-stock products
    private Integer minStock;               // Minimum stock quantity
    private Integer maxStock;               // Maximum stock quantity
    
    // ===== PHYSICAL PROPERTIES =====
    private Format format;                  // Product format (ENUM: HARDCOVER, PAPERBACK, etc.)
    private String ageGroup;                // Target age group
    private String language;                // Product language
    private Integer minPages;               // Minimum page count
    private Integer maxPages;               // Maximum page count
    
    // ===== SERIES FILTERS =====
    private String seriesId;                // products in specific series
    private String seriesName;              // Series name search
    private Integer seriesVolume;           // Specific volume in series
    private Integer minSeriesVolume;        // Minimum series volume
    private Integer maxSeriesVolume;        // Maximum series volume
    
    // ===== FEATURED FILTERS =====
    private String featuredType;            // Featured type (new, bestseller, etc.)
    private Boolean isFeatured;             // Only featured products
    private Boolean isNewRelease;           // Only new releases
    private Boolean isBestseller;           // Only bestsellers
    private Boolean isRecommended;          // Only recommended products
    
    // ===== DATE FILTERS =====
    private Integer publishedYear;          // Published in specific year
    private Integer publishedYearFrom;      // Published from year
    private Integer publishedYearTo;        // Published to year
    private LocalDateTime publishedAfter;   // Published after date
    private LocalDateTime publishedBefore;  // Published before date
    private LocalDateTime createdAfter;     // Created after date
    private LocalDateTime createdBefore;    // Created before date
    private LocalDateTime updatedAfter;     // Updated after date
    private LocalDateTime updatedBefore;    // Updated before date
    
    // ===== CONTENT FILTERS =====
    private List<String> subjects;          // Product subjects
    private List<String> tags;              // Product tags
    private String isbn;                    // ISBN search
    private String code;                    // Product code search
    private String edition;                 // Product edition
    
    // ===== SALES FILTERS =====
    private Integer minTotalSold;           // Minimum total sold
    private Integer maxTotalSold;           // Maximum total sold
    private Double minSalesRevenue;         // Minimum sales revenue
    private Double maxSalesRevenue;         // Maximum sales revenue
    private Integer minDailySold;           // Minimum daily sold
    private Integer maxDailySold;           // Maximum daily sold
    private Integer minWeeklySold;          // Minimum weekly sold
    private Integer maxWeeklySold;          // Maximum weekly sold
    private Integer minMonthlySold;         // Minimum monthly sold
    private Integer maxMonthlySold;         // Maximum monthly sold
    
    // ===== STATUS FILTERS =====
    private String status;                  // Product status (ACTIVE, INACTIVE, etc.)
    private Boolean isActive;               // Only active products
    
    // ===== SORTING =====
    private String sortBy;                  // Sort field (title, price, rating, published, created, stock, sales)
    private String sortDirection;           // Sort direction (asc, desc)
    private List<String> multiSort;         // Multiple sort fields
    
    // ===== PAGINATION & PERFORMANCE =====
    private Boolean includeTotalCount;      // Whether to include total count (for performance)
    private List<String> includeFields;    // Only return specific fields (projection)
    private List<String> excludeFields;    // Exclude specific fields
    private Integer maxResults;             // Maximum number of results to return
    
    // ===== ADVANCED FILTERS =====
    private String titleContains;           // Title contains specific text
    private String descriptionContains;     // Description contains specific text
    private Boolean hasImages;              // Only products with images
    private Boolean hasPreview;             // Only products with preview
    private String difficulty;              // Difficulty level
    private String targetAudience;          // Target audience
}
