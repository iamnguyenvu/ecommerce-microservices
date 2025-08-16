package com.nguyenvu.ecommercems.productservice.service.product.impl;

import com.nguyenvu.ecommercems.productservice.dto.ApiResponse;
import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.StockMovementDTO;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.product.api.InventoryService;
import com.nguyenvu.ecommercems.productservice.service.product.base.AbstractProductService;
import com.nguyenvu.ecommercems.productservice.service.shared.cache.ProductCacheService;
import com.nguyenvu.ecommercems.productservice.service.shared.constants.ProductServiceConstants;

import com.nguyenvu.ecommercems.productservice.service.shared.event.publisher.ProductDomainEventPublisher;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductNotFoundException;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl extends AbstractProductService implements InventoryService {
    private final ProductMapper productMapper;
    private final ProductCacheService cacheService;
    private final ProductDomainEventPublisher eventPublisher;
    private final ProductRepository productRepository;

    @Override
    public ApiResponse updateStock(String productId, Integer quantity) {
        log.info("Updating stock for Product ID: {} with quantity: {}", productId, quantity);

        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

            Integer currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
            product.setStockQuantity(quantity);
            product.setAvailableQuantity(quantity - product.getReservedQuantity());

            updateAvailabilityStatus(product, quantity);

            Product saved = productRepository.save(product);
            
            // Clear cache
            cacheService.evictById(productId);
            
            // Publish event
            eventPublisher.publishStockChangedEvent(
                productId, currentStock, quantity, "Manual stock update", LocalDateTime.now());
                
            return ApiResponse.success("Stock updated successfully");

        } catch (Exception e) {
            log.error("Failed to update stock for Product ID: {}", productId, e);
            return ApiResponse.error("Failed to update stock: " + e.getMessage());
        } finally {
            log.debug("Stock update operation completed for Product ID: {}", productId);
        }
    }

    @Override
    public ApiResponse reserveStock(String productId, Integer quantity, String orderId) {
        log.debug("Reserving {} units for Product ID: {}", quantity, productId);
        
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
                
            Integer currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
            if (currentStock < quantity) {
                return ApiResponse.error("Insufficient stock available");
            }

            product.setReservedQuantity(product.getReservedQuantity() + quantity);
            product.setAvailableQuantity(product.getStockQuantity() - product.getReservedQuantity());

            productRepository.save(product);
            
            log.info("Reserved {} units for Product ID: {}", quantity, productId);
            return ApiResponse.success("Stock reserved successfully");
            
        } catch (Exception e) {
            log.error("Failed to reserve stock for Product ID: {}", productId, e);
            return ApiResponse.error("Failed to reserve stock: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse releaseReservedStock(String productId, Integer quantity, String orderId) {
        log.debug("Releasing reserved stock for Product ID: {}", orderId);
        try {
            Product product = productRepository.findById(orderId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

            Integer reservedQuantity = product.getReservedQuantity();
            if (reservedQuantity <= 0) {
                return ApiResponse.error("No reserved stock to release");
            }

            product.setReservedQuantity(Math.max(0, reservedQuantity - quantity));
            product.setAvailableQuantity(product.getStockQuantity() - product.getReservedQuantity());

            productRepository.save(product);
            cacheService.evictById(productId);

            log.info("Released reserved stock for Product ID: {}", orderId);
            return ApiResponse.success("Reserved stock released successfully");

        } catch (Exception e) {
            log.error("Failed to release reserved stock for order ID: {}", orderId, e);
            return ApiResponse.error("Failed to release reserved stock: " + e.getMessage());
        } finally {
            log.debug("Release reserved stock operation completed for order ID: {}", orderId);
        }
    }

    @Override
    public ApiResponse confirmReservedStock(String orderId) {
        log.info("Confirming reserved stock for order ID: {}", orderId);

        try {
            return null;
        } catch (Exception e) {
            log.error("Failed to confirm reserved stock for order ID: {}", orderId, e);
            return ApiResponse.error("Failed to confirm reserved stock: " + e.getMessage());
        } finally {
            log.debug("Confirm reserved stock operation completed for order ID: {}", orderId);
        }
    }

    @Override
    public List<StockMovementDTO> getStockHistory(String productId, LocalDateTime from, LocalDateTime to) {
        log.debug("Getting stock history for Product ID: {} from {} to {}", productId, from, to);

        try {
            return productRepository.findStockMovementsByBookIdAndDateRange(productId, from, to);
        } catch (Exception e) {
            log.error("Failed to get stock history for Product ID: {}", productId, e);
            return List.of();
        } finally {
            log.debug("Stock history retrieval operation completed for Product ID: {}", productId);
        }
    }

    @Override
    public Integer getCurrentStock(String productId) {
        log.debug("Getting current stock for Product ID: {}", productId);
        
        try {
            Product Product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
                
            Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
            log.debug("Current stock for Product ID {}: {}", productId, stock);
            return stock;
            
        } catch (Exception e) {
            log.error("Failed to get current stock for Product ID: {}", productId, e);
            return 0;
        }
    }

    @Override
    public Integer getAvailableStock(String productId) {
        log.debug("Getting available stock for Product ID: {}", productId);
        
        try {
            Product Product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
            Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
            return stock;
            
        } catch (Exception e) {
            log.error("Failed to get available stock for Product ID: {}", productId, e);
            throw new ProductServiceException("Failed to get available stock for Product ID: " + productId, e);
        }
    }

    @Override
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        log.debug("Getting products with stock below threshold: {}", threshold);
        
        try {
            Integer effectiveThreshold = threshold != null ? threshold : ProductServiceConstants.LOW_STOCK_THRESHOLD;
            
            List<Product> allProducts = productRepository.findAll();
            List<ProductDTO> lowStockProducts = allProducts.stream()
                    .filter(Product -> {
                        Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
                        return stock <= effectiveThreshold;
                    }).map(productMapper::toDTO)
                .collect(Collectors.toList());
                
            log.info("Found {} products with low stock (threshold: {})", lowStockProducts.size(), effectiveThreshold);
            return lowStockProducts;
            
        } catch (Exception e) {
            log.error("Failed to get low stock products", e);
            return List.of();
        }
    }

    @Override
    public List<ProductDTO> getOutOfStockProducts() {
        log.debug("Getting out of stock products");

        try {
            List<Product> allProducts = productRepository.findAll();
            List<ProductDTO> outOfStockProducts = allProducts.stream()
                    .filter(Product -> {
                        Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
                        return stock <= 0;
                    }).map(productMapper::toDTO)
                .collect(Collectors.toList());

            log.info("Found {} products that are out of stock", outOfStockProducts.size());
            return outOfStockProducts;

        } catch (Exception e) {
            log.error("Failed to get out of stock products", e);
            return List.of();
        }
    }

    @Override
    public boolean isInStock(String productId) {
        log.debug("Checking if Product ID {} is still in stock", productId);
        
        try {
            Integer availableStock = getAvailableStock(productId);
            boolean inStock = availableStock > 0;
            
            log.debug("Product ID {} is still in stock: {}", productId, inStock);
            return inStock;
            
        } catch (Exception e) {
            log.error("Failed to check stock for Product ID: {}", productId, e);
            return false;
        }
    }

    @Override
    public boolean isAvailable(String productId, Integer quantity) {
        log.debug("Checking if Product ID {} is available", productId);

        try {
            Integer availableStock = getAvailableStock(productId);
            boolean available = availableStock >= (quantity != null ? quantity : 1);

            log.debug("Product ID {} is available: {}", productId, available);
            return available;

        } catch (Exception e) {
            log.error("Failed to check availability for Product ID: {}", productId, e);
            return false;
        }
    }

    private void updateAvailabilityStatus(Product product, Integer quantity) {
        if (quantity <= 0) {
            product.setAvailability(Availability.OUT_OF_STOCK);
        } else if (quantity <= 5) {
            product.setAvailability(Availability.LOW_STOCK);
        } else {
            product.setAvailability(Availability.IN_STOCK);
        }
    }
}
