package com.nguyenvu.ecommercems.productservice.service.feature.category;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Category Service for managing Product categories
 * Essential for Product organization and filtering
 */
@Service
public interface CategoryService {
    
    /**
     * Get all categories in hierarchy
     */
    List<CategoryDTO> getAllCategories();
    
    /**
     * Get products by category
     */
    Page<ProductDTO> getProductsByCategory(String categoryId, Pageable pageable);
    
    /**
     * Get products by multiple categories
     */
    Page<ProductDTO> getProductsByCategories(List<String> categoryIds, Pageable pageable);
    
    /**
     * Get category hierarchy tree
     */
    CategoryTreeDTO getCategoryTree();
    
    /**
     * Get popular categories (most products)
     */
    List<CategoryDTO> getPopularCategories(int limit);
    
    /**
     * Search categories by name
     */
    List<CategoryDTO> searchCategories(String searchTerm);
}

// Supporting DTOs
class CategoryDTO {
    private String id;
    private String name;
    private String description;
    private String parentId;
    private Integer bookCount;
    private boolean active;
    // getters, setters, constructors
}

class CategoryTreeDTO {
    private String id;
    private String name;
    private List<CategoryTreeDTO> children;
    private Integer totalProducts;
    // getters, setters, constructors
}
