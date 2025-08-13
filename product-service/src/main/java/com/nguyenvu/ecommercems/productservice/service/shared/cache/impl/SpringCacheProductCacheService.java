package com.nguyenvu.ecommercems.productservice.service.shared.cache.impl;

import com.nguyenvu.ecommercems.productservice.service.shared.cache.ProductCacheService;
import com.nguyenvu.ecommercems.productservice.service.shared.constants.ProductServiceConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Redis/Spring Cache implementation of ProductCacheService
 */
@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class SpringCacheBookCacheService implements ProductCacheService {
    
    private final CacheManager cacheManager;

    @Override
    public void evictById(String bookId) {
        log.debug("Evicting cache for Product ID: {}", bookId);
        
        evictFromCache(ProductServiceConstants.CACHE_BOOK_BY_ID, bookId);
        evictFromCache(ProductServiceConstants.CACHE_BOOK_BY_CODE, bookId);
        evictFromCache(ProductServiceConstants.CACHE_BOOK_BY_ISBN, bookId);
        
        log.debug("Cache evicted for Product ID: {}", bookId);
    }

    @Override
    public void evictAll() {
        log.debug("Evicting all products cache");
        
        clearCache(ProductServiceConstants.CACHE_ALL_Products);
        clearCache(ProductServiceConstants.CACHE_FEATURED_Products);
        clearCache(ProductServiceConstants.CACHE_BESTSELLERS);
        
        log.debug("All products cache evicted");
    }

    @Override
    public void evictByCategory(String categoryId) {
        log.debug("Evicting cache for category: {}", categoryId);
        
        evictFromCache(ProductServiceConstants.CACHE_Products_BY_CATEGORY, categoryId);
        
        log.debug("Cache evicted for category: {}", categoryId);
    }

    @Override
    public void evictByAuthor(String authorId) {
        log.debug("Evicting cache for Supplier: {}", authorId);
        
        evictFromCache(ProductServiceConstants.CACHE_Products_BY_AUTHOR, authorId);
        
        log.debug("Cache evicted for Supplier: {}", authorId);
    }

    @Override
    public void evictByPublisher(String publisherId) {
        log.debug("Evicting cache for publisher: {}", publisherId);
        
        evictFromCache(ProductServiceConstants.CACHE_Products_BY_PUBLISHER, publisherId);
        
        log.debug("Cache evicted for publisher: {}", publisherId);
    }

    @Override
    public void evictSearchCaches() {
        log.debug("Evicting search results cache");
        
        clearCache(ProductServiceConstants.CACHE_SEARCH_RESULTS);
        
        log.debug("Search results cache evicted");
    }
    
    @Override
    public void evictByIsbn(String isbn) {
        log.debug("Evicting cache for ISBN: {}", isbn);
        
        evictFromCache(ProductServiceConstants.CACHE_BOOK_BY_ISBN, isbn);
        
        log.debug("Cache evicted for ISBN: {}", isbn);
    }
    
    @Override
    public void evictByCode(String code) {
        log.debug("Evicting cache for code: {}", code);
        
        evictFromCache(ProductServiceConstants.CACHE_BOOK_BY_CODE, code);
        
        log.debug("Cache evicted for code: {}", code);
    }

    /**
     * Helper method to evict specific cache entry
     */
    private void evictFromCache(String cacheName, String key) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
                log.trace("Evicted key '{}' from cache '{}'", key, cacheName);
            } else {
                log.warn("Cache '{}' not found", cacheName);
            }
        } catch (Exception e) {
            log.error("Failed to evict key '{}' from cache '{}': {}", key, cacheName, e.getMessage());
        }
    }

    /**
     * Helper method to clear entire cache
     */
    private void clearCache(String cacheName) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.trace("Cleared cache '{}'", cacheName);
            } else {
                log.warn("Cache '{}' not found", cacheName);
            }
        } catch (Exception e) {
            log.error("Failed to clear cache '{}': {}", cacheName, e.getMessage());
        }
    }
}
