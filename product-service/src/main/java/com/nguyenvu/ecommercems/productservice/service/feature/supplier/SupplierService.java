package com.nguyenvu.ecommercems.productservice.service.feature.supplier;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.SupplierDTO;
import com.nguyenvu.ecommercems.productservice.dto.SupplierStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Supplier Service for managing Product Suppliers
 * Essential for Supplier-based Product discovery and management
 */
@Service
public interface SupplierService {
    
    /**
     * Get all Suppliers with pagination
     */
    Page<SupplierDTO> getAllSuppliers(Pageable pageable);
    
    /**
     * Get Supplier by ID
     */
    SupplierDTO getSupplierById(String supplierId);
    
    /**
     * Get products by Supplier
     */
    Page<ProductDTO> getProductsBySupplier(String supplierId, Pageable pageable);
    
    /**
     * Get products by multiple Suppliers
     */
    Page<ProductDTO> getProductsBySuppliers(List<String> supplierIds, Pageable pageable);
    
    /**
     * Search Suppliers by name
     */
    List<SupplierDTO> searchSuppliers(String searchTerm);
    
    /**
     * Get popular Suppliers (by Product count or ratings)
     */
    List<SupplierDTO> getPopularSuppliers(int limit);
    
    /**
     * Get Supplier statistics
     */
    SupplierStatsDTO getSupplierStats(String supplierId);
}
