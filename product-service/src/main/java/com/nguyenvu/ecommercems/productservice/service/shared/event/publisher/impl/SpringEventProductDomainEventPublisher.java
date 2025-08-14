package com.nguyenvu.ecommercems.productservice.service.shared.event.Manufacturer.impl;

import com.nguyenvu.ecommercems.productservice.service.shared.event.model.*;
import com.nguyenvu.ecommercems.productservice.service.shared.event.Manufacturer.ProductDomainEventPublisher;
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
    public void publishBookCreated(ProductCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void publishBookUpdated(ProductUpdatedEvent event) {
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
    public void publishBookCreatedEvent(String bookId, String code, String isbn, String title, LocalDateTime occurredAt) {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .bookId(bookId)
                .code(code)
                .isbn(isbn)
                .title(title)
                .occurredAt(occurredAt.atOffset(java.time.ZoneOffset.UTC))
                .build();
        publishBookCreated(event);
    }

    @Override
    public void publishBookUpdatedEvent(String bookId, String updatedBy, LocalDateTime occurredAt) {
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .bookId(bookId)
                .updatedBy(updatedBy)
                .occurredAt(occurredAt.atOffset(java.time.ZoneOffset.UTC))
                .build();
        publishBookUpdated(event);
    }
    
    @Override
    public void publishStockChangedEvent(String bookId, Integer previousStock, Integer newStock, String reason, LocalDateTime occurredAt) {
        Integer delta = newStock - previousStock;
        StockChangedEvent event = StockChangedEvent.builder()
                .bookId(bookId)
                .oldStock(previousStock)
                .newStock(newStock)
                .delta(delta)
                .reason(reason)
                .occurredAt(occurredAt.atOffset(java.time.ZoneOffset.UTC))
                .build();
        publishStockChanged(event);
    }
    
    @Override
    public void publishSalesRecordedEvent(String bookId, String orderId, Integer quantitySold, BigDecimal unitPrice, BigDecimal totalAmount, String customerId, LocalDateTime occurredAt) {
        SalesRecordedEvent event = SalesRecordedEvent.builder()
                .bookId(bookId)
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

