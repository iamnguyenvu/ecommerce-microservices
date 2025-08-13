package com.nguyenvu.ecommercems.productservice.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sales {
    private Long totalSold;
    private Integer yearlySold;
    private Integer monthlySold;
    private Integer weeklySold;
    private Integer dailySold;
    private LocalDateTime lastSaleDate;
}
