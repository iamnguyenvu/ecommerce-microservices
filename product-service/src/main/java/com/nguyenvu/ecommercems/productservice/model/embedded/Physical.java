package com.nguyenvu.ecommercems.productservice.model.embedded;

import com.nguyenvu.ecommercems.productservice.model.enums.Format;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Physical {
    private Format format;
    private String language;         // "vi", "en"
    private Integer pageCount;
    private Dimensions dimensions;
    private Integer weight;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Dimensions {
        private Double length;
        private Double width;
        private Double thickness;
    }
}
