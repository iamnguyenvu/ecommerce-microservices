package com.nguyenvu.ecommercems.productservice.service.product;

import com.nguyenvu.ecommercems.productservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Service Interface
 */
public interface ProductService {

    // ===== BASIC CRUD OPERATIONS =====
    Page<ProductDTO> getAllProducts(Pageable pageable);
    
    ProductDTO getProductById(String id);
    
    ProductDTO getProductByCode(String code);
    
    ProductDTO getProductBySku(String sku);
    
    ProductDTO saveProduct(ProductDTO productDTO);
    
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    
    void deleteProduct(String id);

    // ===== SEARCH OPERATIONS =====
    List<ProductDTO> searchProducts(String query);
    
    Page<ProductDTO> searchProductsWithFilters(ProductSearchCriteria criteria, Pageable pageable);

    // ===== CATEGORY OPERATIONS =====
    List<ProductDTO> getProductsByCategory(String categoryId, int limit);
    
    List<ProductDTO> getProductsInMultipleCategories(List<String> categoryIds);

    // ===== PRICING OPERATIONS =====
    List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int limit);
    
    List<ProductDTO> getDiscountedProducts();

    // ===== STOCK OPERATIONS =====
    void updateStock(String id, Integer quantity);
    
    List<ProductDTO> getLowStockProducts(Integer threshold);

    // ===== RECOMMENDATION OPERATIONS =====
    List<ProductDTO> getNewReleases(int limit);
    
    List<ProductDTO> getBestsellers(int limit);
    
    List<ProductDTO> getDailyBestsellers(int limit);
    
    List<ProductDTO> getWeeklyBestsellers(int limit);
    
    List<ProductDTO> getMonthlyBestsellers(int limit);
    
    List<ProductDTO> getRecommendedProducts();
    
    List<ProductDTO> getSimilarProducts(String id, int limit);

    // ===== SUPPLIER OPERATIONS =====
    List<ProductDTO> getProductsBySupplier(String supplierId, int limit);
    
    List<ProductDTO> getProductsBySupplierName(String supplierName, int limit);

    // ===== MANUFACTURER OPERATIONS =====
    List<ProductDTO> getProductsByManufacturer(String manufacturerId, int limit);
    
    List<ProductDTO> getProductsByManufacturerName(String manufacturerName, int limit);

    // ===== SERIES OPERATIONS =====
    List<ProductDTO> getProductsBySeries(String seriesId, int limit);

    // ===== RATING OPERATIONS =====
    boolean canUserRate(String productId, String userId);
    
    ApiResponse addRating(String productId, ProductRatingRequest request);
    
    RatingStatsDTO getRatingStats(String productId);
}

