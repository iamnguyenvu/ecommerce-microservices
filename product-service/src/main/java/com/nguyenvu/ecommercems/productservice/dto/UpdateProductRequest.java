package com.nguyenvu.ecommercems.productservice.dto;

import com.nguyenvu.ecommercems.productservice.model.enums.ProductType;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductStatus;
import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    
    private String title;
    private String subtitle;
    private String description;
    
    // Manufacturing info updates
    private Integer manufacturedYear;
    private String version;
    
    // Supplier updates
    private List<CreateProductRequest.SupplierRequest> suppliers;
    
    // Category updates
    private List<CreateProductRequest.ProductCategoryRequest> categories;
    private List<String> subjects;
    
    // Pricing updates
    private CreateProductRequest.PricingRequest pricing;
    
    // Image updates
    private CreateProductRequest.ImagesRequest images;
    
    // Status updates
    private ProductStatus status;
    private Availability availability;
    
    // SEO updates
    private CreateProductRequest.SeoRequest seo;
    private List<String> tags;
    
    // Feature flags
    private Boolean isFeatured;
}
