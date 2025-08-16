package com.nguyenvu.ecommercems.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationRequest {
    @NotNull
    @Min(1)
    private Integer quantity;

    @NotBlank
    private String orderId;

    private String customerId;

    private LocalDateTime reservationExpiry;
}
