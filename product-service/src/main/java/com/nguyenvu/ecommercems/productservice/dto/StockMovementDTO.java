package com.nguyenvu.ecommercems.productservice.dto;

import com.nguyenvu.ecommercems.productservice.model.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {
    private String id;
    private String bookId;
    private String title;
    private Integer previousStock;
    private Integer nextStock;
    private Integer delta;
    private MovementType movementType;
    private String reason;
    private String orderId; // For reserve/release movements
    private LocalDateTime timestamp;
    private String performedBy; // User who performed the movement
}
