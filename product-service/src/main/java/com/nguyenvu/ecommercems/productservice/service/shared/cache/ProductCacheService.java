package com.nguyenvu.ecommercems.productservice.service.shared.cache;

import java.util.List;

/**
 * Cache service interface for Product-related cache operations
 * 
 * Can be implemented using Spring Cache, Redis, or other caching solutions
 */
public interface ProductCacheService {
    
    /**
     * Evict cache entry by Product ID
     * @param bookId Product ID to evict from cache
     */
    void evictById(String bookId);
    
    /**
     * Evict cache entry by ISBN
     * @param isbn ISBN to evict from cache
     */
    void evictBySku(String isbn);
    
    /**
     * Evict cache entry by Product code
     * @param code Product code to evict from cache
     */
    void evictByCode(String code);
    
    /**
     * Evict all search-related caches
     * Should be called after create/update/delete operations
     */
    void evictSearchCaches();
    
    /**
     * Evict all Product-related caches
     * Use sparingly as this can impact performance
     */
    void evictAll();
    
    /**
     * Evict category-related caches
     * @param categoryId Category ID
     */
    void evictByCategory(String categoryId);
    
    /**
     * Evict Supplier-related caches
     * @param supplierId Supplier ID
     */
    void evictBySupplier(String supplierId);
    
    /**
     * Evict Manufacturer-related caches
     * @param publisherId Manufacturer ID
     */
    void evictByPublisher(String publisherId);
}

