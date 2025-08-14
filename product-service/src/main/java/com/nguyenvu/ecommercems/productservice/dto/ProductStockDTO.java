package com.nguyenvu.ecommercems.productservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for product stock information
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductStockDTO {
    
    // ===== BASIC STOCK INFO =====
    @NotNull(message = "Product ID is required")
    private String productId;
    
    @NotNull(message = "SKU is required")
    private String sku;
    
    @Min(value = 0, message = "Quantity must not be negative")
    private Integer quantity;
    
    @Min(value = 0, message = "Reserved quantity must not be negative")
    private Integer reservedQuantity;
    
    @Min(value = 0, message = "Available quantity must not be negative")
    private Integer availableQuantity;
    
    // ===== STOCK THRESHOLDS =====
    @Min(value = 0, message = "Low stock threshold must not be negative")
    private Integer lowStockThreshold;
    
    @Min(value = 0, message = "Out of stock threshold must not be negative")
    private Integer outOfStockThreshold;
    
    @Min(value = 0, message = "Reorder point must not be negative")
    private Integer reorderPoint;
    
    @Min(value = 1, message = "Reorder quantity must be positive")
    private Integer reorderQuantity;
    
    // ===== AVAILABILITY STATUS =====
    private Availability availability;
    private Boolean inStock;
    private Boolean lowStock;
    private Boolean preorder;
    private Boolean backorder;
    
    // ===== LOCATION & WAREHOUSE =====
    private String warehouseId;
    private String warehouseName;
    private String location;
    private String binLocation;
    
    // ===== STOCK MOVEMENT =====
    private List<StockMovement> recentMovements;
    private Integer totalMovements;
    
    // ===== TIMESTAMPS =====
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastRestocked;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedRestock;
    
    // ===== NESTED CLASSES =====
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StockMovement {
        private String movementId;
        private String type; // IN, OUT, RESERVED, RELEASED, ADJUSTMENT
        private Integer quantity;
        private Integer previousQuantity;
        private Integer newQuantity;
        private String reason;
        private String reference; // Order ID, Purchase Order, etc.
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;
        
        private String createdBy;
        private String notes;
    }
    
    // ===== HELPER METHODS =====
    
    /**
     * Calculate available quantity
     */
    public Integer calculateAvailableQuantity() {
        if (quantity == null || reservedQuantity == null) {
            return 0;
        }
        return Math.max(0, quantity - reservedQuantity);
    }
    
    /**
     * Check if product is in stock
     */
    public Boolean isInStock() {
        Integer available = calculateAvailableQuantity();
        return available > 0;
    }
    
    /**
     * Check if product is low stock
     */
    public Boolean isLowStock() {
        if (lowStockThreshold == null || quantity == null) {
            return false;
        }
        return quantity <= lowStockThreshold && quantity > 0;
    }
    
    /**
     * Check if product is out of stock
     */
    public Boolean isOutOfStock() {
        if (outOfStockThreshold == null || quantity == null) {
            return quantity == null || quantity <= 0;
        }
        return quantity <= outOfStockThreshold;
    }
    
    /**
     * Check if reorder is needed
     */
    public Boolean needsReorder() {
        if (reorderPoint == null || quantity == null) {
            return false;
        }
        return quantity <= reorderPoint;
    }
    
    /**
     * Get availability status
     */
    public Availability getAvailabilityStatus() {
        if (isOutOfStock()) {
            return Availability.OUT_OF_STOCK;
        } else if (isLowStock()) {
            return Availability.LOW_STOCK;
        } else if (preorder != null && preorder) {
            return Availability.PRE_ORDER;
        } else if (backorder != null && backorder) {
            return Availability.BACK_ORDER;
        } else {
            return Availability.IN_STOCK;
        }
    }
    
    // ===== FACTORY METHODS =====
    
    /**
     * Create stock info from basic data
     */
    public static ProductStockDTO create(String productId, String sku, Integer quantity) {
        return ProductStockDTO.builder()
                .productId(productId)
                .sku(sku)
                .quantity(quantity)
                .reservedQuantity(0)
                .availableQuantity(quantity)
                .inStock(quantity > 0)
                .availability(quantity > 0 ? Availability.IN_STOCK : Availability.OUT_OF_STOCK)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create stock info with thresholds
     */
    public static ProductStockDTO createWithThresholds(String productId, String sku, Integer quantity,
                                                      Integer lowStockThreshold, Integer reorderPoint) {
        ProductStockDTO stock = create(productId, sku, quantity);
        stock.setLowStockThreshold(lowStockThreshold);
        stock.setReorderPoint(reorderPoint);
        stock.setLowStock(quantity <= lowStockThreshold && quantity > 0);
        return stock;
    }
}
