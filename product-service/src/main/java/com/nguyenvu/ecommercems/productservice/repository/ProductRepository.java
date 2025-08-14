package com.nguyenvu.ecommercems.productservice.repository;

import com.nguyenvu.ecommercems.productservice.dto.StockMovementDTO;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nguyenvu.ecommercems.productservice.repository.constants.QueryConstants.*;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    // ===== BASIC FINDERS =====
    @Query("{'code': ?0, " + ACTIVE_Products_FILTER + "}")
    Optional<Product> findByCode(String code);

    @Query("{'isbn': ?0, " + ACTIVE_Products_FILTER + "}")
    Optional<Product> findByIsbn(String isbn);

    @Query("{'status': ?0, 'availability':  ?1, " + ACTIVE_Products_FILTER + "}")
    Optional<Product> findByStatusAndAvailability(ProductStatus status, Availability availability);

    // ===== VALIDATION METHODS =====
    @Query(value = "{'code': ?0}", count = true)
    long countByCode(String code);

    @Query(value = "{'isbn': ?0}", count = true)
    long countByIsbn(String isbn);

    @Query("{'code': ?0, '_id': {'$ne': ?1}}")
    boolean existsByCodeAndIdNot(String code, String id);

    @Query("{'isbn': ?0}")
    boolean existsByIsbn(String isbn);

    @Query("{'isbn': ?0, '_id': {'$ne': ?1}}")
    boolean existsByIsbnAndIdNot(String isbn, String id);

    @Query("{'sku': ?0}")
    boolean existsBySku(String sku);

    @Query("{'sku': ?0, '_id': {'$ne': ?1}}")
    boolean existsBySkuAndIdNot(String sku, String id);

    @Query("{'code': ?0}")
    boolean existsByCode(String code);

    // ===== SEARCH QUERIES =====
    @Query(value = "{'$text': {'$search': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_DATE_DESC + "}")
    List<Product> searchByTitleAndAuthor(String searchText);

    @Query(value = "{'$text': {'$search': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_TITLE_ASC + "}")
    List<Product> searchByTitle(String searchText);

    @Query(value = "{'categories.categoryId': ?0, 'pricing.salePrice': {'$gte': ?1, '$lte': ?2}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_PRICE_ASC + "}")
    List<Product> searchByCategoryAndPriceRange(String categoryId, BigDecimal minPrice, BigDecimal maxPrice);

    @Query(value = "{'$text': {'$search': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_DATE_DESC + "}")
    List<Product> searchByTitleWithSorting(String searchText);

    // ===== CATEGORY QUERIES =====
    @Query(value = "{'categories.categoryId': ?0, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByCategoryId(String categoryId);

    @Query(value = "{'categories.categoryId': {'$in': ?0}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByMultipleCategories(List<String> categories);

    @Query(value = "{'categories.path': ?0, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByCategoryPath(String categoryPath);

    // ===== Supplier QUERIES =====
    @Query(value = "{'Suppliers.authorId': ?0, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByAuthorId(String authorId);

    @Query(value = "{'Suppliers.name': {'$regex': ?0, " + CASE_INSENSITIVE + "}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByAuthorName(String authorName);

    @Query(value = "{'Suppliers.role': ?0, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByAuthorRole(String SupplierRole);

    @Query(value = "{'Suppliers.name': {'$in': ?0}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByMultipleAuthors(List<String> authorNames);

    // ===== Manufacturer QUERIES =====
    @Query(value = "{'Manufacturer.publisherId': ?0, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByPublisherId(String publisherId);

    @Query(value = "{'Manufacturer.name': {'$regex': ?0, " + CASE_INSENSITIVE + "}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByPublisherName(String publisherName);

    @Query(value = "{'publishedDate': {'$gte': ?0, '$lte': ?1}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_PUBLISHED_DESC + "}")
    List<Product> findByPublishedDateRange(LocalDate startDate, LocalDate endDate);

    // ===== SERIES QUERIES =====
    @Query(value = "{'seriesId': ?0, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_SERIES_VOLUME + "}")
    List<Product> findBySeriesId(String seriesId);

    @Query(value = "{'seriesId': ?0, " + ACTIVE_IN_STOCK_FILTER + "}", sort = "{" + SORT_BY_SERIES_VOLUME + "}")
    List<Product> findAvailableProductsInSeries(String seriesId);

    @Query(value = "{'seriesId': ?0, 'seriesVolume': {'$gt': ?1}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_SERIES_VOLUME + "}")
    List<Product> findNextProductsInSeries(String seriesId, Integer currentVolume);

    @Query(value = "{'seriesName': ?0, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_SERIES_VOLUME + "}")
    List<Product> findBySeriesName(String seriesName);

    // ===== PRICING QUERIES =====
    @Query(value = "{'pricing.salePrice': {'$gte': ?0, '$lte': ?1}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_PRICE_ASC + "}")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    @Query(value = "{'$expr': {'$lt': ['$pricing.salePrice', '$pricing.listPrice']}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_PRICE_ASC + "}")
    List<Product> findDiscountedProducts();

    @Query(value = "{'pricing.salePrice': {'$lt': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_PRICE_ASC + "}")
    List<Product> findProductsUnderPrice(BigDecimal price);

    // ===== STOCK & AVAILABILITY QUERIES =====
    @Query("{'availability': " + IN_STOCK + ", 'stockQuantity': {'$gt': 0}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findAvailableProducts();

    @Query("{'stockQuantity': {'$lt': ?0, '$gt': 0}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findLowStockProducts(Integer threshold);

    @Query("{'availability': " + OUT_OF_STOCK + ", " + ACTIVE_Products_FILTER + "}")
    List<Product> findOutOfStockProducts();

    // ===== HIGH-PERFORMANCE QUERIES (Using direct values for frequently called methods) =====
    
    /**
     * High-performance version for most frequently used queries.
     * Uses direct string values for better performance in hot paths.
     */
    @Query("{'availability': 'IN_STOCK', 'stockQuantity': {'$gt': 0}, 'status': 'ACTIVE'}")
    List<Product> findAvailableProductsOptimized();
    
    @Query("{'status': 'ACTIVE', 'availability': 'IN_STOCK'}")
    Page<Product> findActiveInStockProductsOptimized(Pageable pageable);
    
    @Query(value = "{'rating.average': {'$gte': 4.0}, 'status': 'ACTIVE', 'availability': 'IN_STOCK'}", 
           sort = "{'rating.average': -1, 'sales.totalSold': -1}")
    List<Product> findRecommendedProductsOptimized();

    // ===== RATING QUERIES =====

    // ===== RATING QUERIES =====
    @Query(value = "{'rating.average': {'$gte': ?0, '$lte': ?1}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByRatingRange(Double minRating, Double maxRating);

    @Query("{'_id': ?0}")
    @Update("{'$set': " + UPDATE_RATING + "}")
    void updateBookRating(String bookId, Double averageRating, Integer totalRating, LocalDateTime updatedAt);

    /*
        * Increment Product rating count.
     */
    @Query("{'_id': ?0}")
    @Update("{'$inc': {'rating.count': 1, 'updatedAt': ?1}}")
    void incrementRatingCount(String bookId, LocalDateTime updatedAt);

    /*
        * Find Product rating >= threshold.
     */
    @Query(value = "{'rating.average': {'$gte': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_RATING_DESC + "}")
    List<Product> findHighRatedProducts(Double minRating);

    @Query(value = "{'rating.average': {'$gte': ?0}, " + ACTIVE_Products_FILTER + "}", 
            sort = "{'rating.average': -1, 'rating.totalRatings': -1}")
    List<Product> findTopRatedProducts(Double minRating, Pageable pageable);

    @Query(value = "{'rating.average': {'$gte': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_RATING_DESC + "}")
    List<Product> findByMinimumRating(Double minRating);

    @Query(value = "{'rating.average': {'$gte': " + HIGH_RATING_THRESHOLD + "}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_RATING_DESC + "}")
    List<Product> findHighlyRatedProducts();

    @Query(value = "{'rating.count': {'$gte': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_RATING_COUNT_DESC + "}")
    List<Product> findProductsWithMostReviews(Integer minReviewCount);

    // ===== FEATURED QUERIES =====
    @Query(value = "{'featured.featuredType': ?0, " + ACTIVE_Products_FILTER + "}", sort = "{'featured.featuredUntil': -1}")
    List<Product> findFeaturedProductsByType(String featuredType);

    @Query(value = "{" + NEW_RELEASES_FILTER + ", " + ACTIVE_IN_STOCK_FILTER + "}", sort = "{" + SORT_BY_DATE_DESC + "}")
    List<Product> findNewReleases();

    @Query(value = ACTIVE_IN_STOCK_FILTER, sort = "{'sales.totalSold': -1}")
    List<Product> findBestsellers();

    @Query(value = ACTIVE_IN_STOCK_FILTER, sort = "{'sales.dailySold': -1}")
    List<Product> findDailyBestsellers();

    @Query(value = ACTIVE_IN_STOCK_FILTER, sort = "{'sales.weeklySold': -1}")
    List<Product> findWeeklyBestsellers();

    @Query(value = ACTIVE_IN_STOCK_FILTER, sort = "{'sales.monthlySold': -1}")
    List<Product> findMonthlyBestsellers();

    @Query(value = ACTIVE_IN_STOCK_FILTER, sort = "{'sales.yearlySold': -1}")
    List<Product> findYearlyBestsellers();


    // ===== DATE RANGE QUERIES =====
    @Query(value = "{'createdAt': {'$gte': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_DATE_DESC + "}")
    List<Product> findRecentlyAddedProducts(LocalDateTime sinceDate);

    @Query(value = "{'publishedDate': {'$gte': ?0, '$lte': ?1}, " + ACTIVE_Products_FILTER + "}", sort = "{" + SORT_BY_PUBLISHED_DESC + "}")
    List<Product> findProductsPublishedInRange(LocalDate startDate, LocalDate endDate);

    @Query(value = "{'updatedAt': {'$gte': ?0}, " + ACTIVE_Products_FILTER + "}", sort = "{'updatedAt': -1}")
    List<Product> findRecentlyUpdatedProducts(LocalDateTime sinceDate);

    // ===== AGGREGATION QUERIES =====
    @Aggregation(pipeline = {
            "{ '$match': { 'sales.totalSold': { '$gt': 0 }, " + ACTIVE_Products_FILTER + " } }",
            "{ '$sort': { 'sales.totalSold': -1 } }",
            "{ '$limit': 10 }"
    })
    List<Product> findTopBestsellingProducts();

    @Aggregation(pipeline = {
            "{ '$match': { 'rating.count': { '$gte': ?0 }, " + ACTIVE_Products_FILTER + " } }",
            "{ '$sort': { 'rating.average': -1 } }",
            "{ '$limit': 10 }"
    })
    List<Product> findTopRatedProductsWithMinReviews(Integer minReviewCount);

    @Aggregation(pipeline = {
            "{ '$match': { " + ACTIVE_Products_FILTER + " } }",
            "{ '$unwind': '$categories' }",
            "{ '$group': { '_id': '$categories.categoryId', 'count': { '$sum': 1 }, 'averagePrice': { '$avg': '$pricing.salePrice' } } }",
            "{ '$sort': { 'count': -1 } }"
    })
    List<Object> findCategoryStatistics();

    @Aggregation(pipeline = {
            "{ '$match': { " + ACTIVE_Products_FILTER + " } }",
            "{ '$group': { '_id': '$categories.categoryId', 'minPrice': { '$min': '$pricing.salePrice' }, 'maxPrice': { '$max': '$pricing.salePrice' }, 'avgPrice': { '$avg': '$pricing.salePrice' } } }",
            "{ '$sort': { 'avgPrice': 1 } }"
    })
    List<Object> findPriceStatisticsByCategory();

    // ===== CUSTOM BUSINESS QUERIES =====
    @Query(value = "{'rating.average': {'$gte': " + HIGH_RATING_THRESHOLD + "}, " + ACTIVE_IN_STOCK_FILTER + "}", sort = "{" + SORT_BY_RATING_DESC + ", 'sales.totalSold': -1}")
    List<Product> findRecommendedProducts();

    @Query(SIMILAR_Products_QUERY)
    List<Product> findSimilarProducts(List<String> categoryIds, List<String> authorNames, String excludeBookId);

    @Query(value = ACTIVE_IN_STOCK_FILTER, sort = "{'sales.totalSold': -1}")
    List<Product> findCrossSellingOpportunities();

    @Query(value = OUT_OF_STOCK_FILTER)
    List<Product> findProductsNeedingRestock();

    // ===== PAGINATED QUERIES =====
    @Query("{'categories.categoryId': ?0, " + ACTIVE_Products_FILTER + "}")
    Page<Product> findByCategoryIdWithPagination(String categoryId, Pageable pageable);

    @Query("{'pricing.salePrice': {'$gte': ?0, '$lte': ?1}, " + ACTIVE_Products_FILTER + "}")
    Page<Product> findByPriceRangeWithPagination(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("{'Suppliers.name': " + REGEX_IGNORE_CASE + ", " + ACTIVE_Products_FILTER + "}")
    Page<Product> findByAuthorNameWithPagination(String authorName, Pageable pageable);

    // ===== BULK OPERATIONS =====
    @Query("{'_id': {'$in': ?0}}")
    List<Product> findByIdIn(List<String> ids);

    @Query("{'categories.categoryId': {'$in': ?0}, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByCategoryIdIn(List<String> categoryIds);

    // ===== ADVANCED SEARCH SUPPORT =====
    @Query("{'title': " + REGEX_IGNORE_CASE + ", " + ACTIVE_Products_FILTER + "}")
    List<Product> findByTitleContainingIgnoreCase(String title);

    @Query("{'description': " + REGEX_IGNORE_CASE + ", " + ACTIVE_Products_FILTER + "}")
    List<Product> findByDescriptionContainingIgnoreCase(String description);

    @Query("{'physical.language': " + REGEX_IGNORE_CASE + ", " + ACTIVE_Products_FILTER + "}")
    List<Product> findByLanguage(String language);

    @Query("{'physical.format': ?0, " + ACTIVE_Products_FILTER + "}")
    List<Product> findByFormat(String format);

    // ===== STOCK MANAGEMENT QUERIES =====
    @Query(value = "")
    List<StockMovementDTO> findStockMovementsByBookIdAndDateRange(String bookId, LocalDateTime from, LocalDateTime to);

}

