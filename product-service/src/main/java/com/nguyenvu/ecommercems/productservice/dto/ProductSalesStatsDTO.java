package com.nguyenvu.ecommercems.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesStatsDTO {
    private String productId;
    private String productTitle;
    private Integer totalSold;
    private Integer dailySold;
    private Integer weeklySold;
    private Integer monthlySold;
    private BigDecimal totalRevenue;
    private BigDecimal averagePrice;
    private LocalDateTime lastSaleDate;
    private LocalDateTime statsUpdatedAt;
}
