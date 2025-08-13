package com.nguyenvu.ecommercems.productservice.service.shared.event.model;

import lombok.Builder;
import lombok.Value;
import java.time.OffsetDateTime;

/**
 * Domain event fired when Product stock changes
 */
@Value
@Builder
public class StockChangedEvent {
    
    String bookId;
    Integer oldStock;
    Integer newStock;
    Integer delta;
    String reason;
    OffsetDateTime occurredAt;
}
