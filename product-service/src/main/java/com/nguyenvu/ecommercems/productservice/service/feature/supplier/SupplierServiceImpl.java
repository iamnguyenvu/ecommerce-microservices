package com.nguyenvu.ecommercems.productservice.service.feature.supplier;

import com.nguyenvu.ecommercems.productservice.dto.SupplierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SupplierServiceImpl implements SupplierService{
    @Override
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return null;
    }
}
