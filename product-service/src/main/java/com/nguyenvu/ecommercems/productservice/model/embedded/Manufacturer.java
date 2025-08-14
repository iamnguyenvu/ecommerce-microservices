package com.nguyenvu.ecommercems.productservice.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Manufacturer {
    private String manufacturerId;      // Reference to Manufacturer collection (changed from publisherId)
    private String name;                // Cached for display
    private String website;             // Manufacturer website
    private String contactEmail;        // Contact email
}


