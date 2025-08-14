package com.nguyenvu.ecommercems.productservice.model;

import com.nguyenvu.ecommercems.productservice.model.embedded.*;
import com.nguyenvu.ecommercems.productservice.model.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private String id;
    
    // ===== THÃ”NG TIN CÆ  Báº¢N =====
    @Indexed(unique = true)
    private String code;                    // MÃ£ sáº£n pháº©m ná»™i bá»™ (PROD2024001)
    
    @Indexed
    private String sku;                     // Stock Keeping Unit

    private ProductType type;
    
    private String title;
    private String subtitle;
    private String description;
    
    // ===== THÃ”NG TIN Sáº¢N XUáº¤T =====
    private Manufacturer manufacturer;         // Äá»•i tá»« Manufacturer thÃ nh manufacturer
    private Integer manufacturedYear;       // Äá»•i tá»« publishedYear
    private LocalDate releaseDate;          // NgÃ y ra máº¯t sáº£n pháº©m
    private LocalDateTime launchTime;       // Thá»i Ä‘iá»ƒm má»Ÿ bÃ¡n chÃ­nh xÃ¡c  
    private String version;                 // Äá»•i tá»« edition thÃ nh version
    
    // ===== THÃ”NG TIN Váº¬T LÃ =====
    private Physical physical;              // Format, language, dimensions, weight
    
    // ===== NHÃ€ Sáº¢N XUáº¤T =====
    private List<Supplier> suppliers;         // Äá»•i tá»« Suppliers thÃ nh suppliers
    
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
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}


