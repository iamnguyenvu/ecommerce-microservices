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
    
    ProductDTO getBookById(String id);
    
    ProductDTO getBookByCode(String code);
    
    ProductDTO getBookByIsbn(String isbn);
    
    ProductDTO saveBook(ProductDTO productDTO);
    
    ProductDTO updateBook(String id, ProductDTO productDTO);
    
    void deleteBook(String id);

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
    List<ProductDTO> getProductsByAuthor(String authorId, int limit);
    
    List<ProductDTO> getProductsByAuthorName(String authorName, int limit);

    // ===== MANUFACTURER OPERATIONS =====
    List<ProductDTO> getProductsByPublisher(String publisherId, int limit);
    
    List<ProductDTO> getProductsByPublisherName(String publisherName, int limit);

    // ===== SERIES OPERATIONS =====
    List<ProductDTO> getProductsBySeries(String seriesId, int limit);

    // ===== RATING OPERATIONS =====
    boolean canUserRate(String bookId, String userId);
    
    ApiResponse addRating(String bookId, ProductRatingRequest request);
    
    RatingStatsDTO getRatingStats(String bookId);
}

