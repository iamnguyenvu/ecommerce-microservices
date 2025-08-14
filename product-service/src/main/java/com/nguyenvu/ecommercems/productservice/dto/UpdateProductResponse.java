package com.nguyenvu.ecommercems.productservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for product update operations
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProductResponse {
    
    // ===== SUCCESS RESPONSE =====
    private String productId;
    private String message;
    private Boolean success;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // ===== UPDATED PRODUCT SUMMARY =====
    private String name;
    private String sku;
    private ProductType type;
    private String manufacturerName;
    private String version;
    
    // ===== CHANGE TRACKING =====
    private List<String> updatedFields;
    private ChangeDetails changes;
    
    // ===== VALIDATION RESULTS =====
    private List<String> warnings;
    private List<String> validationNotes;
    
    // ===== NESTED CLASSES =====
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChangeDetails {
        private String field;
        private Object oldValue;
        private Object newValue;
        private String changeType; // ADDED, UPDATED, REMOVED
        private String reason;
    }
    
    // ===== FACTORY METHODS =====
    
    /**
     * Create a success response for product update
     */
    public static UpdateProductResponse success(String productId, String message) {
        return UpdateProductResponse.builder()
                .productId(productId)
                .message(message != null ? message : "Product updated successfully")
                .success(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a success response with product details
     */
    public static UpdateProductResponse success(String productId, String name, String sku, 
                                              ProductType type, String manufacturerName) {
        return UpdateProductResponse.builder()
                .productId(productId)
                .message("Product updated successfully")
                .success(true)
                .updatedAt(LocalDateTime.now())
                .name(name)
                .sku(sku)
                .type(type)
                .manufacturerName(manufacturerName)
                .build();
    }
    
    /**
     * Create a success response with change tracking
     */
    public static UpdateProductResponse successWithChanges(String productId, List<String> updatedFields) {
        return UpdateProductResponse.builder()
                .productId(productId)
                .message("Product updated successfully")
                .success(true)
                .updatedAt(LocalDateTime.now())
                .updatedFields(updatedFields)
                .build();
    }
    
    /**
     * Create an error response
     */
    public static UpdateProductResponse error(String productId, String message) {
        return UpdateProductResponse.builder()
                .productId(productId)
                .message(message)
                .success(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a partial success response with warnings
     */
    public static UpdateProductResponse partialSuccess(String productId, String message, 
                                                     List<String> warnings) {
        return UpdateProductResponse.builder()
                .productId(productId)
                .message(message)
                .success(true)
                .updatedAt(LocalDateTime.now())
                .warnings(warnings)
                .build();
    }
}
