package com.nguyenvu.ecommercems.productservice.service.shared.exception;

/**
 * Exception thrown when a Product is not found
 */
public class ProductNotFoundException extends ProductServiceException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
