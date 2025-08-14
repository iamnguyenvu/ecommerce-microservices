package com.nguyenvu.ecommercems.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductResponse {
    
    private String id;
    private String code;
    private String sku;
    private String title;
    private String status;
    private String message;
    private LocalDateTime createdAt;
    
    // Static factory methods
    public static CreateProductResponse success(String id, String code, String sku, String title) {
        return CreateProductResponse.builder()
            .id(id)
            .code(code)
            .sku(sku)
            .title(title)
            .status("SUCCESS")
            .message("Product created successfully")
            .createdAt(LocalDateTime.now())
            .build();
    }
    
    public static CreateProductResponse error(String message) {
        return CreateProductResponse.builder()
            .status("ERROR")
            .message(message)
            .createdAt(LocalDateTime.now())
            .build();
    }
}
