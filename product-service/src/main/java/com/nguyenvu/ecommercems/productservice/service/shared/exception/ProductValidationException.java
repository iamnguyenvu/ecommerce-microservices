package com.nguyenvu.ecommercems.productservice.service.shared.exception;

/**
 * Exception thrown when Product validation fails
 */
public class ProductValidationException extends ProductServiceException {
    
    public ProductValidationException(String message) {
        super(message);
    }
    
    public ProductValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
