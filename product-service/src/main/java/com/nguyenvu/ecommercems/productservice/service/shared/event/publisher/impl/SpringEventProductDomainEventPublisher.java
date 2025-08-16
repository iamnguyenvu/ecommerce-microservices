package com.nguyenvu.ecommercems.productservice.service.shared.event.publisher.impl;

import com.nguyenvu.ecommercems.productservice.service.shared.event.model.*;
import com.nguyenvu.ecommercems.productservice.service.shared.event.publisher.ProductDomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Spring event-based implementation of ProductDomainEventPublisher
 */
@Component
public class SpringEventProductDomainEventPublisher implements ProductDomainEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public SpringEventProductDomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publishProductCreated(ProductCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void publishProductUpdated(ProductUpdatedEvent event) {
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void publishStockChanged(StockChangedEvent event) {
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void publishSalesRecorded(SalesRecordedEvent event) {
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void publishProductCreatedEvent(String productId, String code, String sku, String title, LocalDateTime occurredAt) {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(productId)
                .code(code)
                .sku(sku)
                .title(title)
                .occurredAt(occurredAt.atOffset(java.time.ZoneOffset.UTC))
                .build();
        publishProductCreated(event);
    }

    @Override
    public void publishProductUpdatedEvent(String productId, String updatedBy, LocalDateTime occurredAt) {
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId(productId)
                .updatedBy(updatedBy)
                .occurredAt(occurredAt.atOffset(java.time.ZoneOffset.UTC))
                .build();
        publishProductUpdated(event);
    }
    
    @Override
    public void publishStockChangedEvent(String productId, Integer previousStock, Integer newStock, String reason, LocalDateTime occurredAt) {
        Integer delta = newStock - previousStock;
        StockChangedEvent event = StockChangedEvent.builder()
                .productId(productId)
                .oldStock(previousStock)
                .newStock(newStock)
                .delta(delta)
                .reason(reason)
                .occurredAt(occurredAt.atOffset(java.time.ZoneOffset.UTC))
                .build();
        publishStockChanged(event);
    }
    
    @Override
    public void publishSalesRecordedEvent(String productId, String orderId, Integer quantitySold, BigDecimal unitPrice, BigDecimal totalAmount, String customerId, LocalDateTime occurredAt) {
        SalesRecordedEvent event = SalesRecordedEvent.builder()
                .productId(productId)
                .orderId(orderId)
                .quantitySold(quantitySold)
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .customerId(customerId)
                .occurredAt(occurredAt.atOffset(java.time.ZoneOffset.UTC))
                .build();
        publishSalesRecorded(event);
    }
}

