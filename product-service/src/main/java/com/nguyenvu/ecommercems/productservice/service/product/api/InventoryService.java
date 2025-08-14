package com.nguyenvu.ecommercems.productservice.service.product.api;

import com.nguyenvu.ecommercems.productservice.dto.ApiResponse;
import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.StockMovementDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Inventory Service for managing Product stock levels
 * Essential for e-commerce functionality
 */
@Service
public interface InventoryService {
    
    /**
     * Update stock level for a Product
     */
    ApiResponse updateStock(String bookId, Integer quantity);
    
    /**
     * Reserve stock for pending order
     */
    ApiResponse reserveStock(String bookId, Integer quantity,String orderId);
    
    /**
     * Release reserved stock (when order cancelled)
     */
    ApiResponse releaseReservedStock(String orderId);

    ApiResponse confirmReservedStock(String orderId);

    List<StockMovementDTO> getStockHistory(String bookId, LocalDateTime from, LocalDateTime to);
    
    /**
     * Get current stock level
     */
    Integer getCurrentStock(String bookId);
    
    /**
     * Get available stock (total - reserved)
     */
    Integer getAvailableStock(String bookId);
    
    /**
     * Get products with low stock
     */
    List<ProductDTO> getLowStockProducts(Integer threshold);

    /**
     * Get products with out of stock
     */
    List<ProductDTO> getOutOfStockProducts();
    
    /**
     * Check if Product is in stock
     */
    boolean isInStock(String bookId);

    /**
     * Check if Product is available
     */
    boolean isAvailable(String bookId, Integer quantity);

}
