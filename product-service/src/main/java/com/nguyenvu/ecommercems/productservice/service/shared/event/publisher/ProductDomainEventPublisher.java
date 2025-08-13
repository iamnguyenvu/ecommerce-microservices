package com.nguyenvu.ecommercems.productservice.service.shared.event.publisher;

import com.nguyenvu.ecommercems.productservice.service.shared.event.model.ProductCreatedEvent;
import com.nguyenvu.ecommercems.productservice.service.shared.event.model.ProductUpdatedEvent;
import com.nguyenvu.ecommercems.productservice.service.shared.event.model.StockChangedEvent;
import com.nguyenvu.ecommercems.productservice.service.shared.event.model.SalesRecordedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event publisher for Product-related events
 * 
 * Implementation should be provided by infrastructure layer
 * to avoid coupling service layer with specific messaging technology
 */
public interface ProductDomainEventPublisher {
    
    /**
     * Publish Product created event
     * @param event Product created event details
     */
    void publishBookCreated(ProductCreatedEvent event);
    
    /**
     * Publish Product updated event
     * @param event Product updated event details
     */
    void publishBookUpdated(ProductUpdatedEvent event);
    
    /**
     * Publish stock changed event
     * @param event Stock change event details
     */
    void publishStockChanged(StockChangedEvent event);
    
    /**
     * Publish sales recorded event
     * @param event Sales recorded event details
     */
    void publishSalesRecorded(SalesRecordedEvent event);
    
    // ===== CONVENIENCE METHODS =====
    
    /**
     * Convenience method to publish Product created event
     */
    void publishBookCreatedEvent(String bookId, String code, String isbn, String title, LocalDateTime occurredAt);
    
    /**
     * Convenience method to publish Product updated event
     */
    void publishBookUpdatedEvent(String bookId, String updatedBy, LocalDateTime occurredAt);
    
    /**
     * Convenience method to publish stock changed event
     */
    void publishStockChangedEvent(String bookId, Integer previousStock, Integer newStock, String reason, LocalDateTime occurredAt);
    
    /**
     * Convenience method to publish sales recorded event
     */
    void publishSalesRecordedEvent(String bookId, String orderId, Integer quantitySold, 
            BigDecimal unitPrice, BigDecimal totalAmount, String customerId, LocalDateTime occurredAt);
}
