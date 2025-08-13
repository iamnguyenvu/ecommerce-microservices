package com.nguyenvu.ecommercems.productservice.repository;

/**
 * Centralized Query Constants for MongoDB queries across all microservices.
 * 
 * Design Principles:
 * 1. Reusable across multiple repositories (Product, Order, User, etc.)
 * 2. Performance optimized (compile-time vs runtime evaluation)
 * 3. Easy to extend for future requirements
 * 4. Consistent naming conventions
 * 
 * Usage Examples:
 * - ProductRepository: @Query("{" + ACTIVE_ENTITY_FILTER + ", 'code': ?0}")
 * - UserRepository: @Query("{" + ACTIVE_ENTITY_FILTER + ", 'email': ?0}")
 * - OrderRepository: @Query("{" + ACTIVE_ENTITY_FILTER + ", 'orderId': ?0}")
 */
public interface QueryConstants {

    // ===== UNIVERSAL STATUS FILTERS (Reusable across all entities) =====
    String ACTIVE_STATUS = "'status': 'ACTIVE'";
    String INACTIVE_STATUS = "'status': 'INACTIVE'";
    String DRAFT_STATUS = "'status': 'DRAFT'";
    String ARCHIVED_STATUS = "'status': 'ARCHIVED'";
    String DELETED_STATUS = "'status': 'DELETED'";
    
    // ===== Product-SPECIFIC AVAILABILITY =====
    String IN_STOCK = "'availability': 'IN_STOCK'";
    String OUT_OF_STOCK = "'availability': 'OUT_OF_STOCK'";
    String PRE_ORDER = "'availability': 'PRE_ORDER'";
    String DISCONTINUED = "'availability': 'DISCONTINUED'";
    String BACKORDERED = "'availability': 'BACKORDERED'";
    
    // ===== ORDER-SPECIFIC STATUS (For future OrderRepository) =====
    String PENDING_ORDER = "'orderStatus': 'PENDING'";
    String CONFIRMED_ORDER = "'orderStatus': 'CONFIRMED'";
    String SHIPPED_ORDER = "'orderStatus': 'SHIPPED'";
    String DELIVERED_ORDER = "'orderStatus': 'DELIVERED'";
    String CANCELLED_ORDER = "'orderStatus': 'CANCELLED'";
    
    // ===== USER-SPECIFIC STATUS (For future UserRepository) =====
    String VERIFIED_USER = "'verified': true";
    String UNVERIFIED_USER = "'verified': false";
    String PREMIUM_USER = "'membershipType': 'PREMIUM'";
    String BASIC_USER = "'membershipType': 'BASIC'";
    
    // ===== UNIVERSAL FILTERS (Generic - can be used by any entity) =====
    String ACTIVE_ENTITY_FILTER = ACTIVE_STATUS;
    String VISIBLE_ENTITY_FILTER = ACTIVE_STATUS + ", 'visible': true";
    String PUBLIC_ENTITY_FILTER = ACTIVE_STATUS + ", 'isPublic': true";
    
    // ===== Product BUSINESS FILTERS =====
    String ACTIVE_Products_FILTER = ACTIVE_STATUS;
    String ACTIVE_IN_STOCK_FILTER = ACTIVE_STATUS + ", " + IN_STOCK;
    String ACTIVE_OUT_OF_STOCK_FILTER = ACTIVE_STATUS + ", " + OUT_OF_STOCK;
    String AVAILABLE_Products_FILTER = ACTIVE_STATUS + ", " + IN_STOCK + ", 'stockQuantity': {'$gt': 0}";
    String RECOMMENDED_Products_FILTER = ACTIVE_STATUS + ", " + IN_STOCK + ", 'rating.average': {'$gte': 4.0}";
    String LOW_STOCK_Products_FILTER = ACTIVE_STATUS + ", 'stockQuantity': {'$lt': 10, '$gt': 0}";
    String BESTSELLER_Products_FILTER = ACTIVE_STATUS + ", " + IN_STOCK + ", 'sales.totalSold': {'$gt': 0}";
    String HIGH_RATED_Products_FILTER = ACTIVE_STATUS + ", 'rating.average': {'$gte': 4.0}";
    String RESTOCK_NEEDED_FILTER = ACTIVE_STATUS + ", " + OUT_OF_STOCK;
    String FEATURED_Products_FILTER = ACTIVE_STATUS + ", 'featured.isFeatured': true";
    String DISCOUNTED_Products_FILTER = ACTIVE_STATUS + ", '$expr': {'$lt': ['$pricing.salePrice', '$pricing.listPrice']}";
    
    // ===== SORT PATTERNS (Universal - reusable across entities) =====
    String SORT_BY_ID_ASC = "'_id': 1";
    String SORT_BY_ID_DESC = "'_id': -1";
    String SORT_BY_CREATED_ASC = "'createdAt': 1";
    String SORT_BY_CREATED_DESC = "'createdAt': -1";
    String SORT_BY_UPDATED_ASC = "'updatedAt': 1";
    String SORT_BY_UPDATED_DESC = "'updatedAt': -1";
    String SORT_BY_NAME_ASC = "'name': 1";
    String SORT_BY_NAME_DESC = "'name': -1";
    
    // ===== Product-SPECIFIC SORTS =====
    String SORT_BY_TITLE_ASC = "'title': 1";
    String SORT_BY_TITLE_DESC = "'title': -1";
    String SORT_BY_PRICE_ASC = "'pricing.salePrice': 1";
    String SORT_BY_PRICE_DESC = "'pricing.salePrice': -1";
    String SORT_BY_RATING_DESC = "'rating.average': -1, 'rating.count': -1";
    String SORT_BY_RATING_ASC = "'rating.average': 1, 'rating.count': 1";
    String SORT_BY_SALES_DESC = "'sales.totalSold': -1";
    String SORT_BY_SALES_ASC = "'sales.totalSold': 1";
    String SORT_BY_DAILY_SALES_DESC = "'sales.dailySold': -1";
    String SORT_BY_DAILY_SALES_ASC = "'sales.dailySold': 1";
    String SORT_BY_WEEKLY_SALES_DESC = "'sales.weeklySold': -1";
    String SORT_BY_WEEKLY_SALES_ASC = "'sales.weeklySold': 1";
    String SORT_BY_MONTHLY_SALES_DESC = "'sales.monthlySold': -1";
    String SORT_BY_MONTHLY_SALES_ASC = "'sales.monthlySold': 1";
    String SORT_BY_DATE_DESC = "'createdAt': -1";
    String SORT_BY_DATE_ASC = "'createdAt': 1";
    String SORT_BY_PUBLISHED_DESC = "'publishedDate': -1";
    String SORT_BY_PUBLISHED_ASC = "'publishedDate': 1";
    String SORT_BY_SERIES_VOLUME = "'seriesVolume': 1";
    String SORT_BY_STOCK_ASC = "'stockQuantity': 1";
    String SORT_BY_STOCK_DESC = "'stockQuantity': -1";
    String SORT_BY_FEATURED_DESC = "'featured.featuredUntil': -1";
    String SORT_BY_REVIEW_COUNT_DESC = "'rating.count': -1";
    
    // ===== ORDER-SPECIFIC SORTS (For future use) =====
    String SORT_BY_ORDER_DATE_DESC = "'orderDate': -1";
    String SORT_BY_TOTAL_AMOUNT_DESC = "'totalAmount': -1";
    String SORT_BY_ORDER_ID_ASC = "'orderId': 1";
    
    // ===== REGEX OPTIONS & PATTERNS =====
    String CASE_INSENSITIVE = "'$options': 'i'";
    String REGEX_START_WITH = "'$options': 'i'";
    String REGEX_CONTAINS = "'$options': 'i'";
    String REGEX_EXACT_MATCH = "'$options': ''";
    String EMAIL_REGEX = "'^[\\\\w-\\\\.]+@([\\\\w-]+\\\\.)+[\\\\w-]{2,4}$'";
    String PHONE_REGEX = "'^[\\\\+]?[1-9]?[0-9]{7,15}$'";
    
    // ===== DATE & TIME EXPRESSIONS (Keep SpEL for dynamic dates) =====
    String NOW = "?#{T(java.time.LocalDateTime).now()}";
    String TODAY_START = "?#{T(java.time.LocalDateTime).now().toLocalDate().atStartOfDay()}";
    String YESTERDAY = "?#{T(java.time.LocalDateTime).now().minusDays(1)}";
    String LAST_7_DAYS = "?#{T(java.time.LocalDateTime).now().minusDays(7)}";
    String LAST_30_DAYS = "?#{T(java.time.LocalDateTime).now().minusDays(30)}";
    String LAST_90_DAYS = "?#{T(java.time.LocalDateTime).now().minusDays(90)}";
    String LAST_YEAR = "?#{T(java.time.LocalDateTime).now().minusYears(1)}";
    String CURRENT_MONTH_START = "?#{T(java.time.LocalDateTime).now().withDayOfMonth(1).toLocalDate().atStartOfDay()}";
    String CURRENT_YEAR_START = "?#{T(java.time.LocalDateTime).now().withDayOfYear(1).toLocalDate().atStartOfDay()}";
    
    // ===== NUMERIC THRESHOLDS & CONSTANTS =====
    String HIGH_RATING_THRESHOLD = "4.0";
    String MEDIUM_RATING_THRESHOLD = "3.0";
    String LOW_RATING_THRESHOLD = "2.0";
    String MIN_RATING = "1.0";
    String MAX_RATING = "5.0";
    String LOW_STOCK_THRESHOLD = "10";
    String ZERO_STOCK = "0";
    String MIN_STOCK_THRESHOLD = "5";
    String BESTSELLER_THRESHOLD = "100";
    String DAILY_BESTSELLER_THRESHOLD = "5";
    String WEEKLY_BESTSELLER_THRESHOLD = "25";
    String MONTHLY_BESTSELLER_THRESHOLD = "100";
    
    // ===== PAGINATION DEFAULTS =====
    String DEFAULT_PAGE_SIZE = "20";
    String MAX_PAGE_SIZE = "100";
    String MIN_PAGE_SIZE = "1";
    
    // ===== AGGREGATION HELPERS =====
    String GROUP_BY_STATUS = "'_id': '$status', 'count': {'$sum': 1}";
    String GROUP_BY_DATE = "'_id': {'$dateToString': {'format': '%Y-%m-%d', 'date': '$createdAt'}}, 'count': {'$sum': 1}";
    String GROUP_BY_MONTH = "'_id': {'$dateToString': {'format': '%Y-%m', 'date': '$createdAt'}}, 'count': {'$sum': 1}";
    String GROUP_BY_CATEGORY = "'_id': '$categories.categoryId', 'count': {'$sum': 1}";
    String GROUP_BY_AUTHOR = "'_id': '$Suppliers.name', 'count': {'$sum': 1}";
    String GROUP_BY_PUBLISHER = "'_id': '$publisher.name', 'count': {'$sum': 1}";
    String GROUP_BY_AVAILABILITY = "'_id': '$availability', 'count': {'$sum': 1}";
    
    // ===== MATCH STAGES FOR AGGREGATION =====
    String MATCH_ACTIVE = "{ '$match': { " + ACTIVE_Products_FILTER + " } }";
    String MATCH_IN_STOCK = "{ '$match': { " + ACTIVE_IN_STOCK_FILTER + " } }";
    String MATCH_HIGH_RATED = "{ '$match': { " + HIGH_RATED_Products_FILTER + " } }";
    String MATCH_BESTSELLERS = "{ '$match': { " + BESTSELLER_Products_FILTER + " } }";
    
    // ===== PERFORMANCE OPTIMIZED (Direct string values for hot paths) =====
    String PERF_ACTIVE = "'status': 'ACTIVE'";
    String PERF_ACTIVE_IN_STOCK = "'status': 'ACTIVE', 'availability': 'IN_STOCK'";
    String PERF_AVAILABLE = "'status': 'ACTIVE', 'availability': 'IN_STOCK', 'stockQuantity': {'$gt': 0}";
    String PERF_RECOMMENDED = "'status': 'ACTIVE', 'availability': 'IN_STOCK', 'rating.average': {'$gte': 4.0}";
    String PERF_HIGH_RATED = "'status': 'ACTIVE', 'rating.average': {'$gte': 4.0}";
    String PERF_BESTSELLERS = "'status': 'ACTIVE', 'availability': 'IN_STOCK', 'sales.totalSold': {'$gt': 100}";
    String PERF_LOW_STOCK = "'status': 'ACTIVE', 'stockQuantity': {'$lt': 10, '$gt': 0}";
    String PERF_OUT_OF_STOCK = "'status': 'ACTIVE', 'availability': 'OUT_OF_STOCK'";
    
    // Additional missing constants
    String SORT_BY_RATING_COUNT_DESC = "'rating.count': -1";
    String UPDATE_RATING = "{'rating.average': ?1, 'rating.count': ?2, 'updatedAt': ?3}";
    String NEW_RELEASES_FILTER = "'createdAt': {'$gte': ?#{T(java.time.LocalDateTime).now().minusDays(30)}}";
    String OUT_OF_STOCK_FILTER = "{'availability': ?#{T(com.nguyenvu.Productstorems.ProductService.model.enums.Availability).OUT_OF_STOCK}, " + ACTIVE_Products_FILTER + "}";
    String SIMILAR_Products_QUERY = "{'$and': [" +
            "{'_id': {'$ne': ?2}}," +
            "{'status': ?#{T(com.nguyenvu.Productstorems.ProductService.model.enums.ProductStatus).ACTIVE}}," +
            "{'availability': ?#{T(com.nguyenvu.Productstorems.ProductService.model.enums.Availability).IN_STOCK}}," +
            "{'$or': [" +
            "{'categories.categoryId': {'$in': ?0}}," +
            "{'Suppliers.name': {'$in': ?1}}" +
            "]}" +
            "]}";
    String REGEX_IGNORE_CASE = "{'$regex': ?0, '$options': 'i'}";
    
    // ===== TEXT SEARCH HELPERS =====
    String TEXT_SEARCH_PREFIX = "'$text': {'$search': ";
    String TEXT_SEARCH_SUFFIX = "}";
    
    // ===== COMPLEX QUERY HELPERS =====
    String NOT_EQUAL_PREFIX = "'$ne': ";
    String IN_ARRAY_PREFIX = "'$in': ";
    String GREATER_THAN_PREFIX = "'$gt': ";
    String LESS_THAN_PREFIX = "'$lt': ";
    String GREATER_EQUAL_PREFIX = "'$gte': ";
    String LESS_EQUAL_PREFIX = "'$lte': ";
    String EXISTS_TRUE = "'$exists': true";
    String EXISTS_FALSE = "'$exists': false";
}
