package com.nguyenvu.ecommercems.productservice.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pricing {
    private BigDecimal listPrice;
    private BigDecimal salePrice;
    private Integer discountPercent;
    private String currency;
}
