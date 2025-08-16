package com.nguyenvu.ecommercems.productservice.service.product.impl;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.product.api.ProductCrudService;
import com.nguyenvu.ecommercems.productservice.service.product.base.AbstractProductService;
import com.nguyenvu.ecommercems.productservice.service.product.validation.ProductValidator;
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
 * ProductCrudServiceImpl - Complete CRUD operations with validation, events, and caching
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductCrudServiceImpl extends AbstractProductService implements ProductCrudService {
    
    private final ProductRepository ProductRepository;
    private final ProductMapper ProductMapper;
    private final ProductValidator ProductValidator;
    private final ProductDomainEventPublisher eventPublisher;
    private final ProductCacheService cacheService;

    @Override
    public ProductDTO createProduct(ProductDTO ProductDTO) {
        log.debug("Creating new Product: {}", ProductDTO.getTitle());
        
        try {
            ProductValidator.validateForCreate(ProductDTO);
            ProductDTO savedProduct = saveProductTemplate(ProductDTO);
            
            log.info("Successfully created Product: {} (ID: {})", savedProduct.getTitle(), savedProduct.getId());
            return savedProduct;
            
        } catch (Exception e) {
            log.error("Failed to create Product: {}", ProductDTO.getTitle(), e);
            throw new ProductServiceException("Failed to create Product: " + e.getMessage(), e);
        }
    }

    /**
     * Update existing Product with validation
     */
    @Override
    public ProductDTO updateProduct(ProductDTO ProductDTO) {
        log.debug("Updating Product: {}", ProductDTO.getId());
        
        try {
            if (!StringUtils.hasText(ProductDTO.getId())) {
                throw new ProductValidationException("Product ID is required for update");
            }
            
            Product existingProduct = ProductRepository.findById(ProductDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + ProductDTO.getId()));
            
            ProductValidator.validateForUpdate(ProductDTO.getId(), ProductDTO);
            
            Product updatedProduct = ProductMapper.toEntity(ProductDTO);
            updatedProduct.setId(existingProduct.getId());
            updatedProduct.setCreatedAt(existingProduct.getCreatedAt());
            updatedProduct.setUpdatedAt(LocalDateTime.now());
            
            updatedProduct = updateProductTemplate(existingProduct, updatedProduct);
            
            ProductDTO result = ProductMapper.toDTO(updatedProduct);
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
            
            BigDecimal oldPrice = existingProduct.getPricing().getSalePrice() != null ?
                existingProduct.getPricing().getSalePrice() : existingProduct.getPricing().getListPrice();
            
            existingProduct.getPricing().setSalePrice(newPrice);
            existingProduct.setUpdatedAt(LocalDateTime.now());
            
            Product savedProduct = ProductRepository.save(existingProduct);
            
            eventPublisher.publishProductUpdatedEvent(savedProduct.getId(), "SYSTEM", LocalDateTime.now());
            
            cacheService.evictById(bookId);
            extractCategoryIds(savedProduct).forEach(cacheService::evictByCategory);
            cacheService.evictSearchCaches();
            
            ProductDTO result = ProductMapper.toDTO(savedProduct);
            log.info("Successfully updated price for Product: {} from {} to {}", 
                savedProduct.getTitle(), oldPrice, newPrice);
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
    public void deleteProduct(String bookId) {
        log.debug("Deleting Product ID: {}", bookId);
        
        try {
            if (!StringUtils.hasText(bookId)) {
                throw new ProductValidationException("Product ID is required");
            }
            
            Product existingProduct = ProductRepository.findById(bookId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));
            
            deleteProductTemplate(existingProduct);
            
            log.info("Successfully deleted Product: {} (ID: {})", existingProduct.getTitle(), bookId);
            
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
                .map(this::saveProductTemplate)
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
                deleteProductTemplate(Product);
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
            
            Integer currentStock = existingProduct.getStockQuantity() != null ? existingProduct.getStockQuantity() : 0;
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
            
            existingProduct.setStockQuantity(newStock);
            existingProduct.setUpdatedAt(LocalDateTime.now());
            
            Product savedProduct = ProductRepository.save(existingProduct);
            
            eventPublisher.publishStockChangedEvent(
                bookId, currentStock, newStock, reason, LocalDateTime.now());
            
            cacheService.evictById(bookId);
            cacheService.evictSearchCaches();
            
            log.info("Successfully adjusted stock for Product: {} from {} to {} (reason: {})", 
                savedProduct.getTitle(), currentStock, newStock, reason);
                
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
            if (!StringUtils.hasText(Product.getSku())) {
                Product.setSku(generateProductSku());
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
        
        eventPublisher.publishProductCreatedEvent(
            Product.getId(), 
            Product.getSku(), 
            Product.getSku(),
            Product.getTitle(),
            LocalDateTime.now()
        );
        
        cacheService.evictAll();
        extractCategoryIds(Product).forEach(cacheService::evictByCategory);
        extractSupplierNames(Product).forEach(cacheService::evictBySupplier);
        
        log.debug("After save operations completed for Product: {}", Product.getTitle());
    }

    @Override
    protected void afterUpdate(Product oldProduct, Product newProduct) {
        log.debug("After update operations for Product: {}", newProduct.getTitle());
        
        eventPublisher.publishProductUpdatedEvent(
            newProduct.getId(),
            "SYSTEM",
            LocalDateTime.now()
        );
        
        cacheService.evictById(newProduct.getId());
        extractCategoryIds(newProduct).forEach(cacheService::evictByCategory);
        extractSupplierNames(newProduct).forEach(cacheService::evictBySupplier);
        
        if (!oldProduct.getPricing().equals(newProduct.getPricing())) {
            cacheService.evictSearchCaches();
        }
        
        log.debug("After update operations completed for Product: {}", newProduct.getTitle());
    }

    @Override
    protected void afterDelete(Product Product) {
        log.debug("After delete operations for Product: {}", Product.getTitle());
        
        cacheService.evictById(Product.getId());
        cacheService.evictAll();
        extractCategoryIds(Product).forEach(cacheService::evictByCategory);
        extractSupplierNames(Product).forEach(cacheService::evictBySupplier);
        
        log.debug("After delete operations completed for Product: {}", Product.getTitle());
    }

    // ===== UTILITY METHODS =====

    /**
     * Generate unique SKU for Product
     */
    private String generateProductSku() {
        return "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
    private List<String> extractSupplierNames(Product Product) {
        return Product.getSuppliers() != null ?
            Product.getSuppliers().stream()
                .map(Supplier -> Supplier.getName())
                .collect(Collectors.toList()) : List.of();
    }
}

