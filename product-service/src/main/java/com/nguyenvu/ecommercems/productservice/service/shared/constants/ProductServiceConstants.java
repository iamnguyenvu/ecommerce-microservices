package com.nguyenvu.ecommercems.productservice.service.shared.constants;

/**
 * Constants for Product service operations
 */
public final class ProductServiceConstants {
    
    private ProductServiceConstants() {
        // Utility class
    }
    
    // ===== Cache Names =====
    public static final String CACHE_BOOK_BY_ID = "Product:byId";
    public static final String CACHE_BOOK_BY_ISBN = "Product:byIsbn";
    public static final String CACHE_BOOK_BY_CODE = "Product:byCode";
    public static final String CACHE_BOOK_SEARCH = "Product:search";
    public static final String CACHE_BOOK_BESTSELLERS = "Product:bestsellers";
    public static final String CACHE_BOOK_NEW_RELEASES = "Product:newReleases";
    public static final String CACHE_BOOK_BY_CATEGORY = "Product:byCategory";
    public static final String CACHE_BOOK_BY_AUTHOR = "Product:byAuthor";
    public static final String CACHE_BOOK_BY_PUBLISHER = "Product:byPublisher";
    
    // ===== Additional Cache Names (for SpringCacheBookCacheService) =====
    public static final String CACHE_ALL_Products = "Product:all";
    public static final String CACHE_FEATURED_Products = "Product:featured";
    public static final String CACHE_BESTSELLERS = "Product:bestsellers";
    public static final String CACHE_Products_BY_CATEGORY = "Product:byCategory";
    public static final String CACHE_Products_BY_AUTHOR = "Product:byAuthor";
    public static final String CACHE_Products_BY_PRICE_RANGE = "Product:byPriceRange";
    public static final String CACHE_DISCOUNTED_Products = "Product:discounted";
    public static final String CACHE_Products_BY_AVAILABILITY = "Product:byAvailability";
    public static final String CACHE_OUT_OF_STOCK_Products = "Product:outOfStock";
    public static final String CACHE_Products_BY_PUBLISHER = "Product:byPublisher";
    public static final String CACHE_SEARCH_RESULTS = "Product:searchResults";
    
    // ===== Event Topics (for messaging) =====
    public static final String TOPIC_BOOK_CREATED = "Product.created";
    public static final String TOPIC_BOOK_UPDATED = "Product.updated";
    public static final String TOPIC_BOOK_DELETED = "Product.deleted";
    public static final String TOPIC_STOCK_CHANGED = "Product.stock.changed";
    public static final String TOPIC_SALES_RECORDED = "Product.sales.recorded";
    
    // ===== Validation Constants =====
    public static final int MAX_TITLE_LENGTH = 500;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;
    public static final int MAX_AUTHOR_NAME_LENGTH = 200;
    public static final int MAX_PUBLISHER_NAME_LENGTH = 200;
    public static final int MAX_AUTHORS_PER_Product = 10;
    public static final int MAX_CATEGORIES_PER_Product = 5;
    public static final int MAX_STOCK_QUANTITY = 999999;
    
    // ===== Business Rules =====
    public static final String DEFAULT_BOOK_STATUS = "ACTIVE";
    public static final String DELETED_BOOK_STATUS = "DELETED";
    public static final String INACTIVE_BOOK_STATUS = "INACTIVE";
    
    // ===== Hot Release Constants =====
    public static final int HOT_RELEASE_MIN_ADVANCE_HOURS = 1;
    public static final int HOT_RELEASE_RECOMMENDED_START_HOUR = 6;
    public static final int HOT_RELEASE_RECOMMENDED_END_HOUR = 23;
    public static final int MAX_FUTURE_PUBLISH_YEARS = 2;
    
    // ===== Search & Pagination =====
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_SEARCH_LIMIT = 50;
    
    // ===== Bulk Operations =====
    public static final int MAX_BULK_CREATE_SIZE = 100;
    public static final int MAX_BULK_DELETE_SIZE = 100;
    public static final int MAX_BULK_UPDATE_SIZE = 100;
    
    // ===== Sales & Analytics =====
    public static final int BESTSELLERS_DEFAULT_LIMIT = 10;
    public static final int NEW_RELEASES_DEFAULT_LIMIT = 20;
    public static final int RECOMMENDATIONS_DEFAULT_LIMIT = 10;
    
    // ===== Stock Management =====
    public static final int LOW_STOCK_THRESHOLD = 10;
    public static final int OUT_OF_STOCK_THRESHOLD = 0;
    
    // ===== Audit & Logging =====
    public static final String OPERATION_CREATE = "CREATE";
    public static final String OPERATION_UPDATE = "UPDATE";
    public static final String OPERATION_DELETE = "DELETE";
    public static final String OPERATION_STOCK_ADJUST = "STOCK_ADJUST";
    public static final String OPERATION_SALES_RECORD = "SALES_RECORD";
}
