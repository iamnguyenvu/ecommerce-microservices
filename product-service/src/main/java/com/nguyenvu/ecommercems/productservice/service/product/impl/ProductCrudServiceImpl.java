package com.nguyenvu.ecommercems.productservice.service.Product.impl;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.Product.api.ProductCrudService;
import com.nguyenvu.ecommercems.productservice.service.Product.base.AbstractProductservice;
import com.nguyenvu.ecommercems.productservice.service.Product.validation.ProductValidator;
import com.nguyenvu.ecommercems.productservice.service.shared.cache.ProductCacheService;
import com.nguyenvu.ecommercems.productservice.service.shared.constants.ProductServiceConstants;
import com.nguyenvu.ecommercems.productservice.service.shared.event.publisher.ProductDomainEventPublisher;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductNotFoundException;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductServiceException;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BookCrudServiceImpl - Complete CRUD operations with validation, events, and caching
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookCrudServiceImpl extends AbstractProductservice implements ProductCrudService {
    
    private final ProductRepository ProductRepository;
    private final ProductMapper ProductMapper;
    private final ProductValidator ProductValidator;
    private final ProductDomainEventPublisher eventPublisher;
    private final ProductCacheService cacheService;

    /**
     * Create a new Product with full validation and event publishing
     */
    @Override
    public ProductDTO createBook(ProductDTO ProductDTO) {
        log.debug("Creating new Product: {}", ProductDTO.getTitle());
        
        try {
            ProductValidator.validateForCreate(ProductDTO);
            ProductDTO savedProduct = saveBookTemplate(ProductDTO);
            
            log.info("Successfully created Product: {} (ID: {})", savedBook.getTitle(), savedBook.getId());
            return savedBook;
            
        } catch (Exception e) {
            log.error("Failed to create Product: {}", ProductDTO.getTitle(), e);
            throw new ProductServiceException("Failed to create Product: " + e.getMessage(), e);
        }
    }

    /**
     * Update existing Product with validation
     */
    @Override
    public ProductDTO updateBook(ProductDTO ProductDTO) {
        log.debug("Updating Product: {}", ProductDTO.getId());
        
        try {
            if (!StringUtils.hasText(ProductDTO.getId())) {
                throw new ProductValidationException("Product ID is required for update");
            }
            
            Product existingProduct = ProductRepository.findById(ProductDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + ProductDTO.getId()));
            
            ProductValidator.validateForUpdate(ProductDTO.getId(), ProductDTO);
            
            Product updatedProduct = ProductMapper.toEntity(ProductDTO);
            updatedBook.setId(existingBook.getId());
            updatedBook.setCreatedAt(existingBook.getCreatedAt());
            updatedBook.setUpdatedAt(LocalDateTime.now());
            
            updatedProduct = updateBookTemplate(existingBook, updatedBook);
            
            ProductDTO result = ProductMapper.toDTO(updatedBook);
            log.info("Successfully updated Product: {} (ID: {})", result.getTitle(), result.getId());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to update Product ID: {}", ProductDTO.getId(), e);
            throw new ProductServiceException("Failed to update Product: " + e.getMessage(), e);
        }
    }

    /**
     * Update Product price specifically with validation
     */
    @Override
    public ProductDTO patchPrice(String bookId, Double price) {
        log.debug("Updating price for Product ID: {} to {}", bookId, price);
        
        try {
            if (!StringUtils.hasText(bookId)) {
                throw new ProductValidationException("Product ID is required");
            }
            
            if (price == null) {
                throw new ProductValidationException("Price is required");
            }
            
            BigDecimal newPrice = BigDecimal.valueOf(price);
            ProductValidator.validatePriceChange(bookId, newPrice);
            
            Product existingProduct = ProductRepository.findById(bookId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));
            
            BigDecimal oldPrice = existingBook.getPricing().getSalePrice() != null ? 
                existingBook.getPricing().getSalePrice() : existingBook.getPricing().getListPrice();
            
            existingBook.getPricing().setSalePrice(newPrice);
            existingBook.setUpdatedAt(LocalDateTime.now());
            
            Product savedProduct = ProductRepository.save(existingBook);
            
            eventPublisher.publishBookUpdatedEvent(savedBook.getId(), "SYSTEM", LocalDateTime.now());
            
            cacheService.evictById(bookId);
            extractCategoryIds(savedBook).forEach(cacheService::evictByCategory);
            cacheService.evictSearchCaches();
            
            ProductDTO result = ProductMapper.toDTO(savedBook);
            log.info("Successfully updated price for Product: {} from {} to {}", 
                savedBook.getTitle(), oldPrice, newPrice);
            return result;
            
        } catch (Exception e) {
            log.error("Failed to update price for Product ID: {}", bookId, e);
            throw new ProductServiceException("Failed to update Product price: " + e.getMessage(), e);
        }
    }

    /**
     * Delete Product with cascade cleanup
     */
    @Override
    public void deleteBook(String bookId) {
        log.debug("Deleting Product ID: {}", bookId);
        
        try {
            if (!StringUtils.hasText(bookId)) {
                throw new ProductValidationException("Product ID is required");
            }
            
            Product existingProduct = ProductRepository.findById(bookId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));
            
            deleteBookTemplate(existingBook);
            
            log.info("Successfully deleted Product: {} (ID: {})", existingBook.getTitle(), bookId);
            
        } catch (Exception e) {
            log.error("Failed to delete Product ID: {}", bookId, e);
            throw new ProductServiceException("Failed to delete Product: " + e.getMessage(), e);
        }
    }

    /**
     * Bulk create products with transaction management
     */
    @Override
    public List<ProductDTO> createProducts(List<ProductDTO> bookDTOs) {
        log.debug("Creating {} products in bulk", bookDTOs.size());
        
        try {
            if (bookDTOs.size() > ProductServiceConstants.MAX_BULK_CREATE_SIZE) {
                throw new ProductValidationException("Bulk create size cannot exceed " + 
                    ProductServiceConstants.MAX_BULK_CREATE_SIZE + " products");
            }
            
            for (int i = 0; i < bookDTOs.size(); i++) {
                try {
                    ProductValidator.validateForCreate(bookDTOs.get(i));
                } catch (ProductValidationException e) {
                    throw new ProductValidationException("Validation failed for Product at index " + i + ": " + e.getMessage());
                }
            }
            
            List<ProductDTO> results = bookDTOs.stream()
                .map(this::saveBookTemplate)
                .collect(Collectors.toList());
            
            log.info("Successfully created {} products in bulk", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Failed to create products in bulk", e);
            throw new ProductServiceException("Failed to create products in bulk: " + e.getMessage(), e);
        }
    }

    /**
     * Bulk delete products
     */
    @Override
    public void deleteProductsByIds(List<String> bookIds) {
        log.debug("Deleting {} products in bulk", bookIds.size());
        
        try {
            if (bookIds.size() > ProductServiceConstants.MAX_BULK_DELETE_SIZE) {
                throw new ProductValidationException("Bulk delete size cannot exceed " + 
                    ProductServiceConstants.MAX_BULK_DELETE_SIZE + " products");
            }
            
            List<Product> existingProducts = ProductRepository.findAllById(bookIds);
            
            if (existingProducts.size() != bookIds.size()) {
                List<String> foundIds = existingProducts.stream().map(Product::getId).collect(Collectors.toList());
                List<String> missingIds = bookIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
                throw new ProductNotFoundException("Some products not found: " + missingIds);
            }
            
            for (Product Product : existingProducts) {
                deleteBookTemplate(Product);
            }
            
            log.info("Successfully deleted {} products in bulk", existingProducts.size());
            
        } catch (Exception e) {
            log.error("Failed to delete products in bulk", e);
            throw new ProductServiceException("Failed to delete products in bulk: " + e.getMessage(), e);
        }
    }

    /**
     * Update stock quantity
     */
    @Override
    public void updateStock(String bookId, Integer quantity) {
        log.debug("Updating stock for Product ID: {} to {}", bookId, quantity);
        
        try {
            adjustStock(bookId, null, "STOCK_UPDATE:" + quantity);
        } catch (Exception e) {
            log.error("Failed to update stock for Product ID: {}", bookId, e);
            throw new ProductServiceException("Failed to update stock: " + e.getMessage(), e);
        }
    }

    /**
     * Adjust stock by delta (increase/decrease)
     */
    @Override
    public void adjustStock(String bookId, Integer delta, String reason) {
        log.debug("Adjusting stock for Product ID: {} by delta {} with reason: {}", bookId, delta, reason);
        
        try {
            if (!StringUtils.hasText(bookId)) {
                throw new ProductValidationException("Product ID is required");
            }
            
            if (!StringUtils.hasText(reason)) {
                throw new ProductValidationException("Reason is required for stock adjustment");
            }
            
            Product existingProduct = ProductRepository.findById(bookId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));
            
            Integer currentStock = existingBook.getStockQuantity() != null ? existingBook.getStockQuantity() : 0;
            Integer newStock;
            
            if (reason.startsWith("STOCK_UPDATE:")) {
                String quantityStr = reason.substring("STOCK_UPDATE:".length());
                newStock = Integer.valueOf(quantityStr);
            } else {
                if (delta == null) {
                    throw new ProductValidationException("Delta is required for stock adjustment");
                }
                newStock = currentStock + delta;
            }
            
            if (newStock < 0) {
                throw new ProductValidationException("Stock cannot be negative. Current: " + currentStock + ", Delta: " + delta);
            }
            
            existingBook.setStockQuantity(newStock);
            existingBook.setUpdatedAt(LocalDateTime.now());
            
            Product savedProduct = ProductRepository.save(existingBook);
            
            eventPublisher.publishStockChangedEvent(
                bookId, currentStock, newStock, reason, LocalDateTime.now());
            
            cacheService.evictById(bookId);
            cacheService.evictSearchCaches();
            
            log.info("Successfully adjusted stock for Product: {} from {} to {} (reason: {})", 
                savedBook.getTitle(), currentStock, newStock, reason);
                
        } catch (Exception e) {
            log.error("Failed to adjust stock for Product ID: {}", bookId, e);
            throw new ProductServiceException("Failed to adjust stock: " + e.getMessage(), e);
        }
    }

    // ===== TEMPLATE METHOD IMPLEMENTATIONS =====

    @Override
    protected void beforeSave(Product Product) {
        log.debug("Before save operations for Product: {}", Product.getTitle());
        
        LocalDateTime now = LocalDateTime.now();
        if (Product.getId() == null) {
            Product.setCreatedAt(now);
            if (!StringUtils.hasText(Product.getCode())) {
                Product.setCode(generateBookCode());
            }
        }
        Product.setUpdatedAt(now);
        
        if (Product.getStockQuantity() == null) {
            Product.setStockQuantity(0);
        }
        
        log.debug("Before save operations completed for Product: {}", Product.getTitle());
    }

    @Override
    protected void afterSave(Product Product) {
        log.debug("After save operations for Product: {}", Product.getTitle());
        
        eventPublisher.publishBookCreatedEvent(
            Product.getId(), 
            Product.getCode(), 
            Product.getIsbn(),
            Product.getTitle(),
            LocalDateTime.now()
        );
        
        cacheService.evictAll();
        extractCategoryIds(Product).forEach(cacheService::evictByCategory);
        extractAuthorNames(Product).forEach(cacheService::evictByAuthor);
        
        log.debug("After save operations completed for Product: {}", Product.getTitle());
    }

    @Override
    protected void afterUpdate(Product oldBook, Product newBook) {
        log.debug("After update operations for Product: {}", newBook.getTitle());
        
        eventPublisher.publishBookUpdatedEvent(
            newBook.getId(),
            "SYSTEM",
            LocalDateTime.now()
        );
        
        cacheService.evictById(newBook.getId());
        extractCategoryIds(newBook).forEach(cacheService::evictByCategory);
        extractAuthorNames(newBook).forEach(cacheService::evictByAuthor);
        
        if (!oldBook.getPricing().equals(newBook.getPricing())) {
            cacheService.evictSearchCaches();
        }
        
        log.debug("After update operations completed for Product: {}", newBook.getTitle());
    }

    @Override
    protected void afterDelete(Product Product) {
        log.debug("After delete operations for Product: {}", Product.getTitle());
        
        cacheService.evictById(Product.getId());
        cacheService.evictAll();
        extractCategoryIds(Product).forEach(cacheService::evictByCategory);
        extractAuthorNames(Product).forEach(cacheService::evictByAuthor);
        
        log.debug("After delete operations completed for Product: {}", Product.getTitle());
    }

    // ===== UTILITY METHODS =====

    /**
     * Generate unique Product code
     */
    private String generateBookCode() {
        return "Product-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Extract category IDs from Product for cache eviction
     */
    private List<String> extractCategoryIds(Product Product) {
        return Product.getCategories() != null ? 
            Product.getCategories().stream()
                .map(cat -> cat.getCategoryId())
                .collect(Collectors.toList()) : List.of();
    }

    /**
     * Extract Supplier names from Product for cache eviction
     */
    private List<String> extractAuthorNames(Product Product) {
        return Product.getAuthors() != null ? 
            Product.getAuthors().stream()
                .map(Supplier -> Supplier.getName())
                .collect(Collectors.toList()) : List.of();
    }
}
