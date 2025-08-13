package com.nguyenvu.ecommercems.productservice.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCategory {
    private String categoryId;          // Reference to Category collection
    private String name;                // Cached for display
    private String path;                // "Sách Tiếng Việt/Kỹ Năng Sống"
}
