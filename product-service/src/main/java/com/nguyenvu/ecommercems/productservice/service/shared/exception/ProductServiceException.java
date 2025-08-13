package com.nguyenvu.ecommercems.productservice.service.shared.exception;

/**
 * Base exception for all Product service related errors
 */
public class ProductServiceException extends RuntimeException {
    
    public ProductServiceException() {
        super();
    }
    
    public ProductServiceException(String message) {
        super(message);
    }
    
    public ProductServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ProductServiceException(Throwable cause) {
        super(cause);
    }
    
    protected ProductServiceException(String message, Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
