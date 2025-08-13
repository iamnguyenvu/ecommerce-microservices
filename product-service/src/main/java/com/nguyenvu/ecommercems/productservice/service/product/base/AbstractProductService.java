package com.nguyenvu.ecommercems.productservice.service.Product.base;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.embedded.Supplier;
import com.nguyenvu.ecommercems.productservice.model.embedded.ProductCategory;
import com.nguyenvu.ecommercems.productservice.model.embedded.Pricing;
import com.nguyenvu.ecommercems.productservice.model.embedded.Publisher;
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
public abstract class AbstractProductservice {
    @Autowired
    protected ProductRepository ProductRepository;

    @Autowired
    protected ProductMapper ProductMapper;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    // ===== VALIDATION METHODS =====
    protected void validateBookData(ProductDTO ProductDTO) {
        if (!StringUtils.hasText(ProductDTO.getCode())) {
            throw new IllegalArgumentException("Product code is required");
        }

        validateISBN(ProductDTO.getIsbn());
        validatePricing(ProductDTO.getPricing());
        validateAuthor(ProductDTO.getAuthors());
        validateStockQuantity(ProductDTO.getStockQuantity());
        validateCategories(ProductDTO.getCategories());
        validatePublisher(ProductDTO.getPublisher());
    }

    protected void validateISBN(String isbn) {
        if (StringUtils.hasText(isbn)) {
            if (isbn.length() != 10 && isbn.length() != 13) {
                throw new IllegalArgumentException("ISBN must be 10 or 13 digits");
            }
            if (!isbn.matches("\\d+")) {
                throw new IllegalArgumentException("ISBN must contain only digits");
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

    protected void validateAuthor(List<Supplier> Suppliers) {
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

    protected void validatePublisher(Publisher publisher) {
        if (publisher != null) {
            if (!StringUtils.hasText(publisher.getPublisherId())) {
                throw new IllegalArgumentException("Publisher ID is required");
            }
            if (!StringUtils.hasText(publisher.getName())) {
                throw new IllegalArgumentException("Publisher name is required");
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

    protected void checkBookExists(String bookId) {
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
    protected final ProductDTO saveBookTemplate(ProductDTO ProductDTO) {
        log.debug("Executing save Product template for: {}", ProductDTO.getTitle());
        
        // Convert to entity
        Product Product = convertToEntity(ProductDTO);
        
        // Execute hook
        beforeSave(Product);
        
        // Save to database
        Product savedProduct = ProductRepository.save(Product);
        
        // Execute hook
        afterSave(savedBook);
        
        // Convert back to DTO
        ProductDTO result = convertToDTO(savedBook);
        
        log.debug("Save Product template completed for: {}", result.getTitle());
        return result;
    }

    /**
     * Template method for updating an existing Product
     */
    protected final Product updateBookTemplate(Product existingBook, Product updatedBook) {
        log.debug("Executing update Product template for: {}", updatedBook.getTitle());
        
        // Execute hook
        beforeUpdate(existingBook, updatedBook);
        
        // Save to database
        Product savedProduct = ProductRepository.save(updatedBook);
        
        // Execute hook
        afterUpdate(existingBook, savedBook);
        
        log.debug("Update Product template completed for: {}", savedBook.getTitle());
        return savedBook;
    }

    /**
     * Template method for deleting a Product
     */
    protected final void deleteBookTemplate(Product Product) {
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
    protected void beforeUpdate(Product existingBook, Product updatedBook) {
        // Default implementation - can be overridden
        log.debug("Default beforeUpdate hook for: {}", updatedBook.getTitle());
    }
    
    /**
     * Hook method called after updating a Product
     */
    protected void afterUpdate(Product existingBook, Product updatedBook) {
        // Default implementation - can be overridden
        log.debug("Default afterUpdate hook for: {}", updatedBook.getTitle());
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
