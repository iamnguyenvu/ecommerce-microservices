package com.nguyenvu.ecommercems.productservice.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Images {
    private String thumbnail;
    private String frontCover;
    private String backCover;
    private List<String> gallery;
}
