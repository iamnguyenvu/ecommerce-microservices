package com.nguyenvu.ecommercems.productservice.service.shared.event.model;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Domain event fired when Product sales are recorded
 */
@Value
@Builder
public class SalesRecordedEvent {
    
    String bookId;
    String orderId;
    Integer quantitySold;
    BigDecimal unitPrice;
    BigDecimal totalAmount;
    String customerId;
    OffsetDateTime occurredAt;
}
