package com.nguyenvu.ecommercems.productservice.service.shared.event.model;

import lombok.Builder;
import lombok.Value;
import java.time.OffsetDateTime;

/**
 * Domain event fired when a Product is updated
 */
@Value
@Builder
public class ProductUpdatedEvent {
    
    String bookId;
    String updatedBy;
    OffsetDateTime occurredAt;
}
