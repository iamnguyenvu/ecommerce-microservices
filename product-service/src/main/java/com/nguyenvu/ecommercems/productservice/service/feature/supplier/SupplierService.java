package com.nguyenvu.ecommercems.productservice.service.feature.supplier;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Supplier Service for managing Product Suppliers
 * Essential for Supplier-based Product discovery and management
 */
@Service
public interface SupplierService {
    
    /**
     * Get all Suppliers with pagination
     */
    Page<SupplierDTO> getAllSuppliers(Pageable pageable);
    
    /**
     * Get Supplier by ID
     */
    SupplierDTO getSupplierById(String supplierId);
    
    /**
     * Get products by Supplier
     */
    Page<ProductDTO> getProductsBySupplier(String supplierId, Pageable pageable);
    
    /**
     * Get products by multiple Suppliers
     */
    Page<ProductDTO> getProductsByAuthors(List<String> authorIds, Pageable pageable);
    
    /**
     * Search Suppliers by name
     */
    List<AuthorDTO> searchAuthors(String searchTerm);
    
    /**
     * Get popular Suppliers (by Product count or ratings)
     */
    List<AuthorDTO> getPopularAuthors(int limit);
    
    /**
     * Get Supplier statistics
     */
    AuthorStatsDTO getAuthorStats(String authorId);
}

// Supporting DTOs
class AuthorDTO {
    private String id;
    private String name;
    private String biography;
    private String nationality;
    private Integer birthYear;
    private String imageUrl;
    private Integer bookCount;
    private Double averageRating;
    // getters, setters, constructors
}

class AuthorStatsDTO {
    private String authorId;
    private String authorName;
    private Integer totalProducts;
    private Double averageRating;
    private Long totalRatings;
    private String mostPopularBook;
    // getters, setters, constructors
}
