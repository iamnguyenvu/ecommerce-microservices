package com.nguyenvu.ecommercems.productservice.service.Product;

import com.nguyenvu.ecommercems.productservice.dto.*;
import com.nguyenvu.ecommercems.productservice.service.Product.ProductserviceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Service Facade
 * Simple delegation to ProductserviceImpl until microservice architecture is completed
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductserviceImpl ProductserviceImpl;

    // ===== BASIC CRUD OPERATIONS =====
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return ProductserviceImpl.getAllProducts(pageable);
    }

    public ProductDTO getBookById(String id) {
        return ProductserviceImpl.getBookById(id);
    }

    public ProductDTO getBookByCode(String code) {
        return ProductserviceImpl.getBookByCode(code);
    }

    public ProductDTO getBookByIsbn(String isbn) {
        return ProductserviceImpl.getBookByIsbn(isbn);
    }

    public ProductDTO saveBook(ProductDTO ProductDTO) {
        return ProductserviceImpl.saveBook(ProductDTO);
    }

    public ProductDTO updateBook(String id, ProductDTO ProductDTO) {
        return ProductserviceImpl.updateBook(id, ProductDTO);
    }

    public void deleteBook(String id) {
        ProductserviceImpl.deleteBook(id);
    }

    // ===== SEARCH OPERATIONS =====
    public List<ProductDTO> searchProducts(String query) {
        return ProductserviceImpl.searchProducts(query);
    }

    public Page<ProductDTO> searchProductsWithFilters(ProductSearchCriteria criteria, Pageable pageable) {
        return ProductserviceImpl.searchProductsWithFilters(criteria, pageable);
    }

    // ===== CATEGORY OPERATIONS =====
    public List<ProductDTO> getProductsByCategory(String categoryId, int limit) {
        return ProductserviceImpl.getProductsByCategory(categoryId, limit);
    }

    public List<ProductDTO> getProductsInMultipleCategories(List<String> categoryIds) {
        return ProductserviceImpl.getProductsInMultipleCategories(categoryIds);
    }

    // ===== PRICING OPERATIONS =====
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int limit) {
        return ProductserviceImpl.getProductsByPriceRange(minPrice, maxPrice, limit);
    }

    public List<ProductDTO> getDiscountedProducts() {
        return ProductserviceImpl.getDiscountedProducts();
    }

    // ===== STOCK OPERATIONS =====
    public void updateStock(String id, Integer quantity) {
        ProductserviceImpl.updateStock(id, quantity);
    }

    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        return ProductserviceImpl.getLowStockProducts(threshold);
    }

    // ===== RECOMMENDATION OPERATIONS =====
    public List<ProductDTO> getNewReleases(int limit) {
        return ProductserviceImpl.getNewReleases(limit);
    }

    public List<ProductDTO> getBestsellers(int limit) {
        return ProductserviceImpl.getBestsellers(limit);
    }

    public List<ProductDTO> getDailyBestsellers(int limit) {
        return ProductserviceImpl.getDailyBestsellers(limit);
    }

    public List<ProductDTO> getWeeklyBestsellers(int limit) {
        return ProductserviceImpl.getWeeklyBestsellers(limit);
    }

    public List<ProductDTO> getMonthlyBestsellers(int limit) {
        return ProductserviceImpl.getMonthlyBestsellers(limit);
    }

    public List<ProductDTO> getRecommendedProducts() {
        return ProductserviceImpl.getRecommendedProducts();
    }

    public List<ProductDTO> getSimilarProducts(String id, int limit) {
        return ProductserviceImpl.getSimilarProducts(id, limit);
    }

    // ===== Supplier OPERATIONS =====
    public List<ProductDTO> getProductsByAuthor(String authorId, int limit) {
        return ProductserviceImpl.getProductsByAuthor(authorId, limit);
    }

    public List<ProductDTO> getProductsByAuthorName(String authorName, int limit) {
        return ProductserviceImpl.getProductsByAuthorName(authorName, limit);
    }

    // ===== PUBLISHER OPERATIONS =====
    public List<ProductDTO> getProductsByPublisher(String publisherId, int limit) {
        return ProductserviceImpl.getProductsByPublisher(publisherId, limit);
    }

    public List<ProductDTO> getProductsByPublisherName(String publisherName, int limit) {
        return ProductserviceImpl.getProductsByPublisherName(publisherName, limit);
    }

    // ===== SERIES OPERATIONS =====
    public List<ProductDTO> getProductsBySeries(String seriesId, int limit) {
        return ProductserviceImpl.getProductsBySeries(seriesId, limit);
    }

    // ===== RATING OPERATIONS =====
    public boolean canUserRate(String bookId, String userId) {
        return ProductserviceImpl.canUserRate(bookId, userId);
    }

    public ApiResponse addRating(String bookId, ProductRatingRequest request) {
        return ProductserviceImpl.addRating(bookId, request);
    }

    public RatingStatsDTO getRatingStats(String bookId) {
        return ProductserviceImpl.getRatingStats(bookId);
    }
}
