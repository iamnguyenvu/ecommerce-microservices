package com.nguyenvu.ecommercems.productservice.exception;

public class UnauthorizedRatingException extends RuntimeException {
    public UnauthorizedRatingException(String message) {
        super(message);
    }
}
