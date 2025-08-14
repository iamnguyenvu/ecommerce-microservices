package com.nguyenvu.ecommercems.productservice.service.product.base;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.embedded.Supplier;
import com.nguyenvu.ecommercems.productservice.model.embedded.ProductCategory;
import com.nguyenvu.ecommercems.productservice.model.embedded.Pricing;
import com.nguyenvu.ecommercems.productservice.model.embedded.Manufacturer;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductStatus;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public abstract class AbstractProductService {
    @Autowired
    protected ProductRepository ProductRepository;

    @Autowired
    protected ProductMapper ProductMapper;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    // ===== VALIDATION METHODS =====
    protected void validateProductData(ProductDTO ProductDTO) {
        if (!StringUtils.hasText(ProductDTO.getCode())) {
            throw new IllegalArgumentException("Product code is required");
        }

        validateSKU(ProductDTO.getSku());
        validatePricing(ProductDTO.getPricing());
        validateSupplier(ProductDTO.getSuppliers());
        validateStockQuantity(ProductDTO.getStockQuantity());
        validateCategories(ProductDTO.getCategories());
        validateManufacturer(ProductDTO.getManufacturer());
    }

    protected void validateSKU(String sku) {
        if (StringUtils.hasText(sku)) {
            if (sku.trim().isEmpty()) {
                throw new IllegalArgumentException("SKU cannot be empty");
            }
            if (sku.length() > 50) {
                throw new IllegalArgumentException("SKU cannot exceed 50 characters");
            }
        }
    }

    protected void validatePricing(Pricing pricing) {
        if (pricing!= null) {
            if (pricing.getListPrice() != null &&
                    pricing.getListPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("List price cannot be negative");
            }
            if (pricing.getSalePrice() != null &&
                    pricing.getSalePrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Sale price cannot be negative");
            }
        }
    }

    protected void validateSupplier(List<Supplier> Suppliers) {
        if (Suppliers != null && !Suppliers.isEmpty()) {
            for (Supplier Supplier : Suppliers) {
                if (!StringUtils.hasText(Supplier.getName())) {
                    throw new IllegalArgumentException("Supplier name is required");
                }
            }
        }
    }

    protected void validateStockQuantity(Integer stockQuantity) {
        if (stockQuantity != null && stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    protected void validateCategories(List<ProductCategory> categories) {
        if (categories != null && !categories.isEmpty()) {
            for (ProductCategory category : categories) {
                if (!StringUtils.hasText(category.getCategoryId())) {
                    throw new IllegalArgumentException("Category ID is required");
                }
                if (!StringUtils.hasText(category.getName())) {
                    throw new IllegalArgumentException("Category name is required");
                }
            }
        }
    }

    protected void validateManufacturer(Manufacturer Manufacturer) {
        if (Manufacturer != null) {
            if (!StringUtils.hasText(Manufacturer.getManufacturerId())) {
                throw new IllegalArgumentException("Manufacturer ID is required");
            }
            if (!StringUtils.hasText(Manufacturer.getName())) {
                throw new IllegalArgumentException("Manufacturer name is required");
            }
        }
    }

    // ===== COMMON UTILITY METHODS =====
    protected ProductDTO convertToDTO(Product Product) {
        return ProductMapper.toDTO(Product);
    }

    protected Product convertToEntity(ProductDTO ProductDTO) {
        return ProductMapper.toEntity(ProductDTO);
    }

    protected void auditOperation(String operation, ProductDTO ProductDTO) {
        log.info("Operation: {}, Product ID: {}, Title: {}", operation, ProductDTO.getId(), ProductDTO.getTitle());
    }

    protected void checkProductExists(String bookId) {
        if (!ProductRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Product with ID " + bookId + " does not exist");
        }
    }

    // ===== COMMON QUERIES =====
    protected Query buildBasicQuery() {
        return new Query(Criteria.where("status").is(ProductStatus.ACTIVE));
    }

    protected void addPaginationAndSort(Query query, Pageable pageable) {
        query.with(pageable);
    }

    protected Page<ProductDTO> getAllProducts(Pageable pageable) {
        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }

        Query query = new Query(Criteria.where("status").is(ProductStatus.ACTIVE));
        query.with(pageable);

        List<Product> products = mongoTemplate.find(query, Product.class);
        long total = mongoTemplate.count(query.skip(0).limit(0), Product.class);

        List<ProductDTO> bookDTOs = products.stream()
                .map(this::convertToDTO)
                .toList();

        return new PageImpl<>(bookDTOs, pageable, total);
    }

    // ===== TEMPLATE METHODS =====
    
    /**
     * Template method for saving a new Product
     */
    protected final ProductDTO saveProductTemplate(ProductDTO ProductDTO) {
        log.debug("Executing save Product template for: {}", ProductDTO.getTitle());
        
        // Convert to entity
        Product Product = convertToEntity(ProductDTO);
        
        // Execute hook
        beforeSave(Product);
        
        // Save to database
        Product savedProduct = ProductRepository.save(Product);
        
        // Execute hook
        afterSave(savedProduct);
        
        // Convert back to DTO
        ProductDTO result = convertToDTO(savedProduct);
        
        log.debug("Save Product template completed for: {}", result.getTitle());
        return result;
    }

    /**
     * Template method for updating an existing Product
     */
    protected final Product updateProductTemplate(Product existingProduct, Product updatedProduct) {
        log.debug("Executing update Product template for: {}", updatedProduct.getTitle());
        
        // Execute hook
        beforeUpdate(existingProduct, updatedProduct);
        
        // Save to database
        Product savedProduct = ProductRepository.save(updatedProduct);
        
        // Execute hook
        afterUpdate(existingProduct, savedProduct);
        
        log.debug("Update Product template completed for: {}", savedProduct.getTitle());
        return savedProduct;
    }

    /**
     * Template method for deleting a Product
     */
    protected final void deleteProductTemplate(Product Product) {
        log.debug("Executing delete Product template for: {}", Product.getTitle());
        
        // Execute hook
        beforeDelete(Product);
        
        // Delete from database
        ProductRepository.deleteById(Product.getId());
        
        // Execute hook
        afterDelete(Product);
        
        log.debug("Delete Product template completed for: {}", Product.getTitle());
    }

    // ===== HOOK METHODS (to be overridden by subclasses) =====
    
    /**
     * Hook method called before saving a new Product
     */
    protected void beforeSave(Product Product) {
        // Default implementation - can be overridden
        log.debug("Default beforeSave hook for: {}", Product.getTitle());
    }
    
    /**
     * Hook method called after saving a new Product
     */
    protected void afterSave(Product Product) {
        // Default implementation - can be overridden
        log.debug("Default afterSave hook for: {}", Product.getTitle());
    }
    
    /**
     * Hook method called before updating a Product
     */
    protected void beforeUpdate(Product existingProduct, Product updatedProduct) {
        // Default implementation - can be overridden
        log.debug("Default beforeUpdate hook for: {}", updatedProduct.getTitle());
    }
    
    /**
     * Hook method called after updating a Product
     */
    protected void afterUpdate(Product existingProduct, Product updatedProduct) {
        // Default implementation - can be overridden
        log.debug("Default afterUpdate hook for: {}", updatedProduct.getTitle());
    }
    
    /**
     * Hook method called before deleting a Product
     */
    protected void beforeDelete(Product Product) {
        // Default implementation - can be overridden
        log.debug("Default beforeDelete hook for: {}", Product.getTitle());
    }
    
    /**
     * Hook method called after deleting a Product
     */
    protected void afterDelete(Product Product) {
        // Default implementation - can be overridden
        log.debug("Default afterDelete hook for: {}", Product.getTitle());
    }
}

