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
    
    // ===== THÔNG TIN CƠ BẢN =====
    @NotBlank(message = "Product code is required")
    private String code;                    // Mã sách nội bộ (BOOK2024001)

    @NotBlank(message = "ISBN is required")
    private String isbn;                    // ISBN-13
    
    @NotBlank(message = "Product title is required")
    private String title;
    
    private String subtitle;
    private String description;
    
    // ===== THÔNG TIN XUẤT BẢN =====
    private Publisher publisher;
    private Integer publishedYear;
    private LocalDate publishedDate;          // Ngày xuất bản (tất cả sách)
    private LocalDateTime releaseTime;        // Thời điểm mở bán chính xác (chỉ sách cực kỳ hot)
    private String edition;
    
    // ===== THÔNG TIN VẬT LÝ =====
    private Physical physical;              // Format, language, pageCount, dimensions, weight
    
    // ===== TÁC GIẢ =====
    private List<Supplier> Suppliers;
    
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
