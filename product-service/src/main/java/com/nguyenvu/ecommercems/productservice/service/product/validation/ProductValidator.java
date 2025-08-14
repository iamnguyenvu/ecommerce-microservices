package com.nguyenvu.ecommercems.productservice.service.product.validation;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.model.embedded.Supplier;
import com.nguyenvu.ecommercems.productservice.model.embedded.ProductCategory;
import com.nguyenvu.ecommercems.productservice.model.embedded.Pricing;
import com.nguyenvu.ecommercems.productservice.model.embedded.Manufacturer;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductValidationException;
import com.nguyenvu.ecommercems.productservice.service.shared.constants.ProductServiceConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductValidator {
    
    private final ProductRepository ProductRepository;
    private final PriceValidator priceValidator;
    private final IsbnValidator isbnValidator;

    /**
     * Validate Product for creation
     */
    public void validateForCreate(ProductDTO Product) {
        log.debug("Validating Product for creation: {}", Product.getTitle());
        
        validateRequired(Product);
        validateBusinessRules(Product);
        validateUniqueness(Product);
    }

    /**
     * Validate Product for update
     */
    public void validateForUpdate(String id, ProductDTO Product) {
        log.debug("Validating Product for update: {}", id);
        
        Objects.requireNonNull(id, "Product ID cannot be null");
        validateRequired(Product);
        validateBusinessRules(Product);
        validateUniquenessForUpdate(id, Product);
    }

    /**
     * Validate price change specifically
     */
    public void validatePriceChange(String bookId, BigDecimal newPrice) {
        log.debug("Validating price change for Product: {} to {}", bookId, newPrice);
        
        Objects.requireNonNull(bookId, "Product ID cannot be null");
        Objects.requireNonNull(newPrice, "New price cannot be null");
        
        priceValidator.validatePrice(newPrice);
        
        // Additional business rules for price changes
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductValidationException("Price must be greater than zero");
        }
    }

    /**
     * Validate required fields
     */
    private void validateRequired(ProductDTO Product) {
        if (!StringUtils.hasText(Product.getTitle())) {
            throw new ProductValidationException("Product title is required");
        }
        
        if (Product.getCategories() == null || Product.getCategories().isEmpty()) {
            throw new ProductValidationException("Product must have at least one category");
        }
        
        if (Product.getPricing() == null) {
            throw new ProductValidationException("Product pricing information is required");
        }
        
        if (Product.getPublisher() == null) {
            throw new ProductValidationException("Product Manufacturer information is required");
        }
        
        // ISBN is optional but if provided must be valid
        if (StringUtils.hasText(Product.getIsbn())) {
            isbnValidator.validate(Product.getIsbn());
        }
    }

    /**
     * Validate business rules
     */
    private void validateBusinessRules(ProductDTO Product) {
        validateTitle(Product.getTitle());
        validateSuppliers(Product.getSuppliers());
        validateCategories(Product.getCategories());
        validatePricing(Product.getPricing());
        validatePublisher(Product.getPublisher());
        validatePublishedDate(Product.getPublishedDate());
        validateHotReleaseTime(Product.getReleaseTime(), Product.getPublishedDate());
        validateStock(Product.getStockQuantity());
        validateDescription(Product.getDescription());
    }

    /**
     * Validate title constraints
     */
    private void validateTitle(String title) {
        if (title.length() > ProductServiceConstants.MAX_TITLE_LENGTH) {
            throw new ProductValidationException("Product title cannot exceed " + ProductServiceConstants.MAX_TITLE_LENGTH + " characters");
        }
        
        if (title.trim().length() < 2) {
            throw new ProductValidationException("Product title must be at least 2 characters long");
        }
    }

    /**
     * Validate Suppliers
     */
    private void validateSuppliers(List<Supplier> Suppliers) {
        if (Suppliers.size() > ProductServiceConstants.MAX_CATEGORIES_PER_PRODUCT) {
            throw new ProductValidationException("Product cannot have more than " + ProductServiceConstants.MAX_CATEGORIES_PER_PRODUCT + " Suppliers");
        }

        for (Supplier Supplier : Suppliers) {
            if (!StringUtils.hasText(Supplier.getName())) {
                throw new ProductValidationException("Supplier name cannot be empty");
            }

            if (Supplier.getName().length() > ProductServiceConstants.MAX_SUPPLIER_NAME_LENGTH) {
                throw new ProductValidationException("Supplier name cannot exceed " + ProductServiceConstants.MAX_SUPPLIER_NAME_LENGTH + " characters");
            }
        }
    }

    /**
     * Validate categories
     */
    private void validateCategories(List<ProductCategory> categories) {
        if (categories.size() > ProductServiceConstants.MAX_CATEGORIES_PER_PRODUCT) {
            throw new ProductValidationException("Product cannot have more than " + ProductServiceConstants.MAX_CATEGORIES_PER_PRODUCT + " categories");
        }
        
        for (ProductCategory category : categories) {
            if (!StringUtils.hasText(category.getCategoryId())) {
                throw new ProductValidationException("Category ID cannot be empty");
            }
            
            if (!StringUtils.hasText(category.getName())) {
                throw new ProductValidationException("Category name cannot be empty");
            }
        }
    }

    /**
     * Validate pricing
     */
    private void validatePricing(Pricing pricing) {
        priceValidator.validatePricing(pricing);
    }

    /**
     * Validate Manufacturer
     */
    private void validatePublisher(Manufacturer Manufacturer) {
        if (!StringUtils.hasText(Manufacturer.getName())) {
            throw new ProductValidationException("Manufacturer name is required");
        }
        
        if (Manufacturer.getName().length() > ProductServiceConstants.MAX_PUBLISHER_NAME_LENGTH) {
            throw new ProductValidationException("Manufacturer name cannot exceed " + ProductServiceConstants.MAX_PUBLISHER_NAME_LENGTH + " characters");
        }
    }

    /**
     * Validate published date (supports both date-only and datetime for hot releases)
     */
    private void validatePublishedDate(LocalDate publishedDate) {
        if (publishedDate != null) {
            LocalDate now = LocalDate.now();
            
            // Cannot be too far in the future (configurable years for planning releases)
            if (publishedDate.isAfter(now.plusYears(ProductServiceConstants.MAX_FUTURE_PUBLISH_YEARS))) {
                throw new ProductValidationException("Published date cannot be more than " + 
                    ProductServiceConstants.MAX_FUTURE_PUBLISH_YEARS + " years in the future");
            }
            
            // Cannot be too far in the past (optional business rule)
            if (publishedDate.isBefore(LocalDate.of(1400, 1, 1))) {
                throw new ProductValidationException("Published date cannot be before year 1400");
            }
        }
    }

    /**
     * Validate release time (chá»‰ cho sÃ¡ch cá»±c ká»³ hot cÃ³ thá»i Ä‘iá»ƒm má»Ÿ bÃ¡n chÃ­nh xÃ¡c)
     */
    private void validateHotReleaseTime(LocalDateTime releaseTime, LocalDate publishedDate) {
        if (releaseTime != null) {
            // Release time pháº£i cÃ³ publishedDate
            if (publishedDate == null) {
                throw new ProductValidationException("Published date is required when release time is specified");
            }
            
            // Release time pháº£i cÃ¹ng ngÃ y vá»›i publishedDate
            if (!releaseTime.toLocalDate().equals(publishedDate)) {
                throw new ProductValidationException("Release time must be on the same date as published date");
            }
            
            LocalDateTime now = LocalDateTime.now();
            
            // Pháº£i lÃªn lá»‹ch trÆ°á»›c Ã­t nháº¥t 2 giá» cho hot release
            if (releaseTime.isBefore(now.plusHours(2))) {
                throw new ProductValidationException("Hot release must be scheduled at least 2 hours in advance");
            }
            
            // Khuyáº¿n cÃ¡o khung giá» há»£p lÃ½ cho hot release (8:00 - 22:00)
            int hour = releaseTime.getHour();
            if (hour < 8 || hour > 22) {
                log.warn("Hot release scheduled for {}:00 - consider scheduling between 8:00-22:00 for better user engagement", hour);
            }
            
            // KhÃ´ng nÃªn lÃªn lá»‹ch quÃ¡ xa trong tÆ°Æ¡ng lai
            if (releaseTime.isAfter(now.plusMonths(6))) {
                throw new ProductValidationException("Release time cannot be more than 6 months in the future");
            }
        }
    }

    /**
     * Validate stock quantity
     */
    private void validateStock(Integer stockQuantity) {
        if (stockQuantity != null) {
            if (stockQuantity < 0) {
                throw new ProductValidationException("Stock quantity cannot be negative");
            }
            
            if (stockQuantity > ProductServiceConstants.MAX_STOCK_QUANTITY) {
                throw new ProductValidationException("Stock quantity cannot exceed " + ProductServiceConstants.MAX_STOCK_QUANTITY);
            }
        }
    }

    /**
     * Validate description
     */
    private void validateDescription(String description) {
        if (StringUtils.hasText(description)) {
            if (description.length() > ProductServiceConstants.MAX_DESCRIPTION_LENGTH) {
                throw new ProductValidationException("Description cannot exceed " + ProductServiceConstants.MAX_DESCRIPTION_LENGTH + " characters");
            }
        }
    }

    /**
     * Validate uniqueness for new products
     */
    private void validateUniqueness(ProductDTO Product) {
        // Check ISBN uniqueness
        if (StringUtils.hasText(Product.getIsbn())) {
            if (ProductRepository.existsByIsbn(Product.getIsbn())) {
                throw new ProductValidationException("Product with ISBN '" + Product.getIsbn() + "' already exists");
            }
        }
        
        // Check code uniqueness if provided
        if (StringUtils.hasText(Product.getCode())) {
            if (ProductRepository.existsByCode(Product.getCode())) {
                throw new ProductValidationException("Product with code '" + Product.getCode() + "' already exists");
            }
        }
    }

    /**
     * Validate uniqueness for updates (excluding current Product)
     */
    private void validateUniquenessForUpdate(String currentBookId, ProductDTO Product) {
        // Check ISBN uniqueness (excluding current Product)
        if (StringUtils.hasText(Product.getIsbn())) {
            if (ProductRepository.existsByIsbnAndIdNot(Product.getIsbn(), currentBookId)) {
                throw new ProductValidationException("Product with ISBN '" + Product.getIsbn() + "' already exists");
            }
        }
        
        // Code typically shouldn't change in updates
        if (StringUtils.hasText(Product.getCode())) {
            if (ProductRepository.existsByCodeAndIdNot(Product.getCode(), currentBookId)) {
                throw new ProductValidationException("Product with code '" + Product.getCode() + "' already exists");
            }
        }
    }
}

