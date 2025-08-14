package com.nguyenvu.ecommercems.productservice.model.enums;

/**
 * Enum representing the type of product based on delivery method and business logic
 */
public enum ProductType {
    
    /**
     * Physical products that require shipping and inventory management
     * Examples: Books, Electronics, Clothing, Furniture
     */
    PHYSICAL,
    
    /**
     * Digital products that can be downloaded or accessed online
     * Examples: E-books, Software, Music, Videos, Courses
     */
    DIGITAL,
    
    /**
     * Services that don't require shipping or inventory
     * Examples: Consulting, Subscriptions, Warranties, Support
     */
    SERVICE
}
