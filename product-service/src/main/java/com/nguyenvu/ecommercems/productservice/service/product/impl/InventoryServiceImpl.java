package com.nguyenvu.ecommercems.productservice.service.Product.impl;

import com.nguyenvu.ecommercems.productservice.dto.ApiResponse;
import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.StockMovementDTO;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.Product.api.InventoryService;
import com.nguyenvu.ecommercems.productservice.service.Product.base.AbstractProductservice;
import com.nguyenvu.ecommercems.productservice.service.shared.constants.ProductServiceConstants;

import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookInventoryServiceImpl extends AbstractProductservice implements InventoryService {
    private final ProductMapper ProductMapper;

    private final ProductRepository ProductRepository;

    @Override
    public ApiResponse updateStock(String bookId, Integer quantity) {
        log.debug("Updating stock for Product ID: {} to quantity: {}", bookId, quantity);
        
        try {
            Product Product = ProductRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + bookId));
                
            Product.setStockQuantity(quantity);
            ProductRepository.save(Product);
            
            log.info("Updated stock for Product ID: {} to {}", bookId, quantity);
            return ApiResponse.success("Stock updated successfully");
            
        } catch (Exception e) {
            log.error("Failed to update stock for Product ID: {}", bookId, e);
            return ApiResponse.error("Failed to update stock: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse reserveStock(String bookId, Integer quantity, String orderId) {
        log.debug("Reserving {} units for Product ID: {}", quantity, bookId);
        
        try {
            // Note: This is a simplified implementation
            // In a real system, you'd have a separate reserved_stock field
            Product Product = ProductRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + bookId));
                
            Integer currentStock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
            if (currentStock < quantity) {
                return ApiResponse.error("Insufficient stock available");
            }
            
            log.info("Reserved {} units for Product ID: {}", quantity, bookId);
            return ApiResponse.success("Stock reserved successfully");
            
        } catch (Exception e) {
            log.error("Failed to reserve stock for Product ID: {}", bookId, e);
            return ApiResponse.error("Failed to reserve stock: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse releaseReservedStock(String orderId) {
        log.debug("Releasing reserved stock for Product ID: {}", orderId);
        try {

            return null;

        } catch (Exception e) {
            log.error("Failed to release reserved stock for order ID: {}", orderId, e);
            return ApiResponse.error("Failed to release reserved stock: " + e.getMessage());
        } finally {
            log.debug("Release reserved stock operation completed for order ID: {}", orderId);
        }
    }

    @Override
    public ApiResponse confirmReservedStock(String orderId) {
        return null;
    }

    @Override
    public List<StockMovementDTO> getStockHistory(String bookId, LocalDateTime from, LocalDateTime to) {
        log.debug("Getting stock history for Product ID: {} from {} to {}", bookId, from, to);

        try {
            return ProductRepository.findStockMovementsByBookIdAndDateRange(bookId, from, to);
        } catch (Exception e) {
            log.error("Failed to get stock history for Product ID: {}", bookId, e);
            return List.of();
        } finally {
            log.debug("Stock history retrieval operation completed for Product ID: {}", bookId);
        }
    }

    @Override
    public Integer getCurrentStock(String bookId) {
        log.debug("Getting current stock for Product ID: {}", bookId);
        
        try {
            Product Product = ProductRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + bookId));
                
            Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
            log.debug("Current stock for Product ID {}: {}", bookId, stock);
            return stock;
            
        } catch (Exception e) {
            log.error("Failed to get current stock for Product ID: {}", bookId, e);
            return 0;
        }
    }

    @Override
    public Integer getAvailableStock(String bookId) {
        log.debug("Getting available stock for Product ID: {}", bookId);
        
        try {
            Product Product = ProductRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + bookId));
            Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
            return stock;
            
        } catch (Exception e) {
            log.error("Failed to get available stock for Product ID: {}", bookId, e);
            throw new ProductServiceException("Failed to get available stock for Product ID: " + bookId, e);
        }
    }

    @Override
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        log.debug("Getting products with stock below threshold: {}", threshold);
        
        try {
            Integer effectiveThreshold = threshold != null ? threshold : ProductServiceConstants.LOW_STOCK_THRESHOLD;
            
            List<Product> allProducts = ProductRepository.findAll();
            List<ProductDTO> lowStockProducts = allProducts.stream()
                    .filter(Product -> {
                        Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
                        return stock <= effectiveThreshold;
                    }).map(ProductMapper::toDTO)
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
            List<Product> allProducts = ProductRepository.findAll();
            List<ProductDTO> outOfStockProducts = allProducts.stream()
                    .filter(Product -> {
                        Integer stock = Product.getStockQuantity() != null ? Product.getStockQuantity() : 0;
                        return stock <= 0;
                    }).map(ProductMapper::toDTO)
                .collect(Collectors.toList());

            log.info("Found {} products that are out of stock", outOfStockProducts.size());
            return outOfStockProducts;

        } catch (Exception e) {
            log.error("Failed to get out of stock products", e);
            return List.of();
        }
    }

    @Override
    public boolean isInStock(String bookId) {
        log.debug("Checking if Product ID {} is still in stock", bookId);
        
        try {
            Integer availableStock = getAvailableStock(bookId);
            boolean inStock = availableStock > 0;
            
            log.debug("Product ID {} is still in stock: {}", bookId, inStock);
            return inStock;
            
        } catch (Exception e) {
            log.error("Failed to check stock for Product ID: {}", bookId, e);
            return false;
        }
    }

    @Override
    public boolean isAvailable(String bookId, Integer quantity) {
        log.debug("Checking if Product ID {} is available", bookId);

        try {
            Integer availableStock = getAvailableStock(bookId);
            boolean available = availableStock >= (quantity != null ? quantity : 1);

            log.debug("Product ID {} is available: {}", bookId, available);
            return available;

        } catch (Exception e) {
            log.error("Failed to check availability for Product ID: {}", bookId, e);
            return false;
        }
    }
}
