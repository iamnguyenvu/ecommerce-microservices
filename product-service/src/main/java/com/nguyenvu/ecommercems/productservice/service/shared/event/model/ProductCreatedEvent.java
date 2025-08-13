package com.nguyenvu.ecommercems.productservice.service.shared.event.model;

import lombok.Builder;
import lombok.Value;
import java.time.OffsetDateTime;

/**
 * Domain event fired when a new Product is created
 */
@Value
@Builder
public class ProductCreatedEvent {
    
    String bookId;
    String code;
    String isbn;
    String title;
    OffsetDateTime occurredAt;
}
