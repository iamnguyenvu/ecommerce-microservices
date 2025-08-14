package com.nguyenvu.ecommercems.productservice.model.embedded;

import com.nguyenvu.ecommercems.productservice.model.enums.SupplierRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Supplier {
    private String name;
    private SupplierRole role;          // PRIMARY, SECONDARY, BACKUP
    private String email;               // Contact email
    private String website;             // Supplier website
    private String country;             // Supplier country
}
