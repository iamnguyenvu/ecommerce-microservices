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
    private SupplierRole role;          // "nhà cung cấp chính", "nhà cung cấp phụ", "nhà phân phối"
    private String country;
}
