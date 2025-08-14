package com.nguyenvu.ecommercems.productservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for product deletion operations
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteProductResponse {
    
    // ===== BASIC RESPONSE =====
    private String productId;
    private String message;
    private Boolean success;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deletedAt;
    
    // ===== DELETION DETAILS =====
    private String productName;
    private String sku;
    private String deletionType; // SOFT_DELETE, HARD_DELETE, ARCHIVE
    private String reason;
    
    // ===== RELATED DATA =====
    private RelatedDataInfo relatedData;
    
    // ===== WARNINGS & VALIDATIONS =====
    private List<String> warnings;
    private List<String> blockers;
    private Boolean forceDelete;
    
    // ===== NESTED CLASSES =====
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RelatedDataInfo {
        private Integer activeOrders;
        private Integer cartItems;
        private Integer wishlistItems;
        private Integer reviews;
        private Integer relatedProducts;
        private List<String> affectedEntities;
        private Boolean hasActiveRelations;
    }
    
    // ===== FACTORY METHODS =====
    
    /**
     * Create a success response for product deletion
     */
    public static DeleteProductResponse success(String productId, String message) {
        return DeleteProductResponse.builder()
                .productId(productId)
                .message(message != null ? message : "Product deleted successfully")
                .success(true)
                .deletedAt(LocalDateTime.now())
                .deletionType("SOFT_DELETE")
                .build();
    }
    
    /**
     * Create a success response with product details
     */
    public static DeleteProductResponse success(String productId, String productName, String sku, 
                                              String deletionType) {
        return DeleteProductResponse.builder()
                .productId(productId)
                .message("Product deleted successfully")
                .success(true)
                .deletedAt(LocalDateTime.now())
                .productName(productName)
                .sku(sku)
                .deletionType(deletionType)
                .build();
    }
    
    /**
     * Create a success response with related data info
     */
    public static DeleteProductResponse successWithRelatedData(String productId, String productName,
                                                             RelatedDataInfo relatedData) {
        return DeleteProductResponse.builder()
                .productId(productId)
                .message("Product deleted successfully")
                .success(true)
                .deletedAt(LocalDateTime.now())
                .productName(productName)
                .deletionType("SOFT_DELETE")
                .relatedData(relatedData)
                .build();
    }
    
    /**
     * Create an error response
     */
    public static DeleteProductResponse error(String productId, String message) {
        return DeleteProductResponse.builder()
                .productId(productId)
                .message(message)
                .success(false)
                .deletedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create an error response with blockers
     */
    public static DeleteProductResponse errorWithBlockers(String productId, String message, 
                                                        List<String> blockers) {
        return DeleteProductResponse.builder()
                .productId(productId)
                .message(message)
                .success(false)
                .deletedAt(LocalDateTime.now())
                .blockers(blockers)
                .build();
    }
    
    /**
     * Create a response with warnings (partial success)
     */
    public static DeleteProductResponse successWithWarnings(String productId, String message,
                                                           List<String> warnings) {
        return DeleteProductResponse.builder()
                .productId(productId)
                .message(message)
                .success(true)
                .deletedAt(LocalDateTime.now())
                .deletionType("SOFT_DELETE")
                .warnings(warnings)
                .build();
    }
    
    /**
     * Create related data info for validation
     */
    public static RelatedDataInfo createRelatedDataInfo(int activeOrders, int cartItems, 
                                                       int wishlistItems, int reviews) {
        return RelatedDataInfo.builder()
                .activeOrders(activeOrders)
                .cartItems(cartItems)
                .wishlistItems(wishlistItems)
                .reviews(reviews)
                .hasActiveRelations(activeOrders > 0 || cartItems > 0)
                .build();
    }
}
