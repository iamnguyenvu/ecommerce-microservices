package com.nguyenvu.ecommercems.productservice.model.enums;

/**
 * Enum representing the condition of physical products
 */
public enum ProductCondition {
    
    /**
     * Brand new product, never used, in original packaging
     */
    NEW,
    
    /**
     * Used product in excellent condition, minor wear
     */
    LIKE_NEW,
    
    /**
     * Used product in good condition, normal wear
     */
    GOOD,
    
    /**
     * Used product with visible wear but fully functional
     */
    FAIR,
    
    /**
     * Used product with significant wear, may have defects
     */
    POOR,
    
    /**
     * Professionally refurbished product with warranty
     */
    REFURBISHED,
    
    /**
     * Product returned and resold at discount
     */
    OPEN_BOX
}
