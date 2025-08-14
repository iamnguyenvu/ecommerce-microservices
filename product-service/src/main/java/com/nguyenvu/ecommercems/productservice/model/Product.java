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
    
    // ===== THÔNG TIN CƠ BẢN =====
    @Indexed(unique = true)
    private String code;                    // Mã sản phẩm nội bộ (PROD2024001)
    
    @Indexed
    private String sku;                     // Stock Keeping Unit

    private ProductType type;
    
    private String title;
    private String subtitle;
    private String description;
    
    // ===== THÔNG TIN SẢN XUẤT =====
    private Manufacturer manufacturer;         // Đổi từ Manufacturer thành manufacturer
    private Integer manufacturedYear;       // Đổi từ publishedYear
    private LocalDate releaseDate;          // Ngày ra mắt sản phẩm
    private LocalDateTime launchTime;       // Thời điểm mở bán chính xác  
    private String version;                 // Đổi từ edition thành version
    
    // ===== THÔNG TIN VẬT LÝ =====
    private Physical physical;              // Format, language, dimensions, weight
    
    // ===== NHÀ SẢN XUẤT =====
    private List<Supplier> suppliers;         // Đổi từ Suppliers thành suppliers
    
    // ===== PHÂN LOẠI =====
    private List<ProductCategory> categories;
    private List<String> subjects;
    private AgeGroup ageGroup;              // enum: CHILDREN, TEEN, ADULT, ALL_AGES
    
    // ===== GIÁ CẢ =====
    private Pricing pricing;
    
    // ===== HÌNH ẢNH =====
    private Images images;
    
    // ===== ĐÁNH GIÁ CHI TIẾT =====
    private Rating rating;
    
    // ===== TRẠNG THÁI & KHO =====
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


