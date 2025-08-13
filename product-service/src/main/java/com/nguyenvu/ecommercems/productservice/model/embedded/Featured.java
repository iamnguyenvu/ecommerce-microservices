package com.nguyenvu.ecommercems.productservice.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Featured {
    private Boolean isFeatured;
    private String featuredType;     // "new", "bestseller", "recommended", "promotion"
    private LocalDateTime featuredUntil;
}
