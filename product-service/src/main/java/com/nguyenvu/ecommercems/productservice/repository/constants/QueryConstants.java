package com.nguyenvu.ecommercems.productservice.repository.constants;

/**
 * MongoDB Query Constants for Product Repository
 */
public final class QueryConstants {

    private QueryConstants() {
        // Utility class
    }

    // ===== FILTER QUERIES =====
    
    /**
     * Filter for active products only
     */
    public static final String ACTIVE_Products_FILTER = """
        { 'status': 'ACTIVE', 'deleted': false }
        """;

    /**
     * Filter for active products that are in stock
     */
    public static final String ACTIVE_IN_STOCK_FILTER = """
        { 
            'status': 'ACTIVE', 
            'deleted': false,
            'availability': 'IN_STOCK',
            'stockQuantity': { '$gt': 0 }
        }
        """;

    /**
     * Filter for out of stock products
     */
    public static final String OUT_OF_STOCK_FILTER = """
        { 
            'status': 'ACTIVE', 
            'deleted': false,
            'availability': 'OUT_OF_STOCK'
        }
        """;

    /**
     * Filter for new releases (last 30 days)
     */
    public static final String NEW_RELEASES_FILTER = """
        {
            'status': 'ACTIVE',
            'deleted': false,
            'createdAt': { 
                '$gte': { '$date': { '$numberLong': '%d' } }
            }
        }
        """;

    /**
     * Filter for products with high ratings (4.0+)
     */
    public static final String HIGH_RATING_THRESHOLD = """
        { 
            'status': 'ACTIVE', 
            'deleted': false,
            'rating.averageRating': { '$gte': 4.0 }
        }
        """;

    // ===== STOCK CONSTANTS =====
    
    public static final String IN_STOCK = """
        { 'availability': 'IN_STOCK', 'stockQuantity': { '$gt': 0 } }
        """;
        
    public static final String OUT_OF_STOCK = """
        { 'availability': 'OUT_OF_STOCK' }
        """;

    // ===== SORT QUERIES =====
    
    /**
     * Sort by creation date descending (newest first)
     */
    public static final String SORT_BY_DATE_DESC = """
        { 'createdAt': -1 }
        """;

    /**
     * Sort by title ascending
     */
    public static final String SORT_BY_TITLE_ASC = """
        { 'name': 1 }
        """;

    /**
     * Sort by price ascending
     */
    public static final String SORT_BY_PRICE_ASC = """
        { 'price': 1 }
        """;

    /**
     * Sort by published date descending
     */
    public static final String SORT_BY_PUBLISHED_DESC = """
        { 'publishedDate': -1 }
        """;

    /**
     * Sort by series and volume
     */
    public static final String SORT_BY_SERIES_VOLUME = """
        { 'series.name': 1, 'series.volume': 1 }
        """;

    /**
     * Sort by rating descending
     */
    public static final String SORT_BY_RATING_DESC = """
        { 'rating.averageRating': -1 }
        """;

    /**
     * Sort by rating count descending
     */
    public static final String SORT_BY_RATING_COUNT_DESC = """
        { 'rating.totalRatings': -1 }
        """;

    // ===== UPDATE QUERIES =====
    
    /**
     * Update rating fields
     */
    public static final String UPDATE_RATING = """
        {
            '$set': {
                'rating.averageRating': ?0,
                'rating.totalRatings': ?1,
                'rating.ratingDistribution': ?2,
                'updatedAt': { '$date': { '$numberLong': '%d' } }
            }
        }
        """;

    // ===== SEARCH QUERIES =====
    
    /**
     * Similar products query (by category and tags)
     */
    public static final String SIMILAR_Products_QUERY = """
        {
            '$or': [
                { 'category.id': ?0 },
                { 'tags': { '$in': ?1 } }
            ],
            'id': { '$ne': ?2 },
            'status': 'ACTIVE',
            'deleted': false
        }
        """;

    // ===== REGEX OPTIONS =====
    
    /**
     * Case insensitive regex option
     */
    public static final String CASE_INSENSITIVE = "i";
    
    /**
     * Regex ignore case option
     */
    public static final String REGEX_IGNORE_CASE = "i";
}
