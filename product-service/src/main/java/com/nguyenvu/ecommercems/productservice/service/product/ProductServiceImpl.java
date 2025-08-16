package com.nguyenvu.ecommercems.productservice.service.product;

import com.nguyenvu.ecommercems.productservice.dto.*;
import com.nguyenvu.ecommercems.productservice.exception.ProductNotFoundException;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.embedded.Supplier;
import com.nguyenvu.ecommercems.productservice.model.embedded.ProductCategory;
import com.nguyenvu.ecommercems.productservice.model.embedded.Rating;
import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductStatus;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository ProductRepository;
    private final ProductMapper ProductMapper;
    private final MongoTemplate mongoTemplate;

    // ===== BASIC CRUD OPERATIONS =====

    /**
     * Get all products with pagination
     */
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.debug("Getting all products with pagination: {}", pageable);
        Page<Product> ProductsPage = ProductRepository.findAll(pageable);
        List<ProductDTO> bookDTOs = ProductsPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();
        return new PageImpl<>(bookDTOs, pageable, ProductsPage.getTotalElements());
    }

    /**
     * Get Product by ID with exception handling
     */
    public ProductDTO getProductById(String id) {
        log.debug("Getting Product by ID: {}", id);
        return ProductRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    /**
     * Get Product by code - Delegated to SKU method
     */
    public ProductDTO getProductByCode(String code) {
        log.debug("Getting Product by code (using SKU): {}", code);
        // For backward compatibility, treat code as SKU
        return getProductBySku(code);
    }

    /**
     * Get Product by SKU
     * SAMPLE IMPLEMENTATION - COMPLETED
     */
    public ProductDTO getProductBySku(String sku) {
        log.debug("Getting Product by SKU: {}", sku);

        // Validate SKU format
        if (!StringUtils.hasText(sku)) {
            throw new IllegalArgumentException("SKU is required");
        }

        // Basic SKU validation (can be enhanced)
        if (sku.length() < 3) {
            throw new IllegalArgumentException("SKU must be at least 3 characters");
        }

        // Call repository method
        return ProductRepository.findBySku(sku)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
    }

    /**
     * Save new Product with validation
     */
    @Transactional
    @Override
    public ProductDTO saveProduct(ProductDTO ProductDTO) {
        log.info("Saving new Product: {}", ProductDTO.getTitle());

        // Basic validation
        validateBookData(ProductDTO);

        // Convert DTO to entity
        Product Product = convertToEntity(ProductDTO);

        // Set default values
        Product.setStatus(ProductStatus.ACTIVE);
        Product.setCreatedAt(LocalDateTime.now());
        Product.setUpdatedAt(LocalDateTime.now());

        // Save to repository
        Product savedProduct = ProductRepository.save(Product);

        log.info("Successfully saved Product with ID: {}", savedProduct.getId());
        return convertToDTO(savedProduct);
    }

    /**
     * Update Product (partial update)
     */
    @Transactional
    @Override
    public ProductDTO updateProduct(String id, ProductDTO ProductDTO) {
        log.info("Updating Product with ID: {}", id);

        Product Product = ProductRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        validateBookData(ProductDTO);

        ProductMapper.updateEntityFromDTO(ProductDTO, Product);

        Product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = ProductRepository.save(Product);
        return convertToDTO(updatedProduct);
    }

    /**
     * Delete Product (soft delete)
     */
    @Transactional
    @Override
    public void deleteProduct(String id) {
        log.info("Soft deleting Product with ID: {}", id);

        Product product = ProductRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        product.setStatus(ProductStatus.INACTIVE);
        product.setUpdatedAt(LocalDateTime.now());

        ProductRepository.save(product);
        log.info("Successfully soft deleted Product with ID: {}", id);
    }

    // ===== SEARCH OPERATIONS =====

    /**
     * Search products by text
     */
    public List<ProductDTO> searchProducts(String searchText) {
        log.debug("Searching products by text: {}", searchText);

        if (!StringUtils.hasText(searchText)) {
            return List.of();
        }

        List<Product> products = ProductRepository.searchByTitleAndAuthor(searchText);
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Advanced search with criteria
     */
    public Page<ProductDTO> searchProductsWithFilters(ProductSearchCriteria criteria, Pageable pageable) {
        log.debug("Advanced search with criteria: {}", criteria);

        // Validate criteria
        if (criteria == null) {
            return getAllProducts(pageable);
        }

        if(pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }

        // Build MongoDB query using criteria API
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(ProductStatus.ACTIVE));

        // Text search using MongoDB full-text search
        if (StringUtils.hasText(criteria.getSearchText())) {
            query.addCriteria(new Criteria("$text").is(
                    new BasicQuery("{\"$search\": \"" + criteria.getSearchText() + "\"}")
            ));
        }

        // Category filter
        if (criteria.getCategoryIds() != null && !criteria.getCategoryIds().isEmpty()) {
            query.addCriteria(Criteria.where("categories.categoryId").in(criteria.getCategoryIds()));
        }

        // Price range filter
        if (criteria.getMinPrice() != null || criteria.getMaxPrice() != null) {
            Criteria priceCriteria = Criteria.where("pricing.salePrice");

            if (criteria.getMinPrice() != null) {
                priceCriteria = priceCriteria.gte(criteria.getMinPrice());
            }

            if (criteria.getMaxPrice() != null) {
                priceCriteria = priceCriteria.lte(criteria.getMaxPrice());
            }

            query.addCriteria(priceCriteria);
        }

        // Supplier filter
        if (StringUtils.hasText(criteria.getSupplierName())) {
            query.addCriteria(Criteria.where("Suppliers.name").regex(criteria.getSupplierName(), "i"));
        }

        // Multiple Suppliers filter
        if (criteria.getSupplierIds() != null && !criteria.getSupplierIds().isEmpty()) {
            query.addCriteria(Criteria.where("Suppliers.supplierId").in(criteria.getSupplierIds()));
        }

        // Manufacturer filter
        if (StringUtils.hasText(criteria.getManufacturerId())) {
            query.addCriteria(Criteria.where("Manufacturer.manufacturerId").is(criteria.getManufacturerId()));
        }

        // Manufacturer name filter
        if (StringUtils.hasText(criteria.getManufacturerName())) {
            query.addCriteria(Criteria.where("Manufacturer.name").regex(criteria.getManufacturerName(), "i"));
        }

        // Availability filter
        if (criteria.getAvailabilities() != null && !criteria.getAvailabilities().isEmpty()) {
            query.addCriteria(Criteria.where("availability").in(criteria.getAvailabilities()));
        }

        // Product type filter (replaces format)
        if (criteria.getProductTypes() != null && !criteria.getProductTypes().isEmpty()) {
            query.addCriteria(Criteria.where("type").in(criteria.getProductTypes()));
        }

        // Rating filter
        if (criteria.getMinRating() != null) {
            query.addCriteria(Criteria.where("rating.average").gte(criteria.getMinRating()));
        }

        // Stock filter
        if (criteria.getMinStock() != null) {
            query.addCriteria(Criteria.where("stockQuantity").gte(criteria.getMinStock()));
        }

        // In stock filter
        if (criteria.getInStock() != null && criteria.getInStock()) {
            query.addCriteria(Criteria.where("availability").is(Availability.IN_STOCK));
            query.addCriteria(Criteria.where("stockQuantity").gt(0));
        }

        // Language filter - commented out as not available in new search criteria
        // TODO: Add language filter to ProductSearchCriteria if needed
        // if (StringUtils.hasText(criteria.getLanguage())) {
        //     query.addCriteria(Criteria.where("physical.language").regex(criteria.getLanguage(), "i"));
        // }

        // Series filter
        if (StringUtils.hasText(criteria.getSeriesId())) {
            query.addCriteria(Criteria.where("series.seriesId").is(criteria.getSeriesId()));
        }

        // Sales filters
        if (criteria.getMinTotalSold() != null) {
            query.addCriteria(Criteria.where("sales.totalSold").gte(criteria.getMinTotalSold()));
        }

        // Detailed sales tracking filters - commented out as not available in new search criteria
        // TODO: Add detailed sales tracking to ProductSearchCriteria if needed
        /*
        if (criteria.getMinDailySold() != null || criteria.getMaxDailySold() != null) {
            Criteria dailySoldCriteria = Criteria.where("sales.dailySold");
            if (criteria.getMinDailySold() != null) {
                dailySoldCriteria = dailySoldCriteria.gte(criteria.getMinDailySold());
            }
            if (criteria.getMaxDailySold() != null) {
                dailySoldCriteria = dailySoldCriteria.lte(criteria.getMaxDailySold());
            }
            query.addCriteria(dailySoldCriteria);
        }

        if (criteria.getMinWeeklySold() != null || criteria.getMaxWeeklySold() != null) {
            Criteria weeklySoldCriteria = Criteria.where("sales.weeklySold");
            if (criteria.getMinWeeklySold() != null) {
                weeklySoldCriteria = weeklySoldCriteria.gte(criteria.getMinWeeklySold());
            }
            if (criteria.getMaxWeeklySold() != null) {
                weeklySoldCriteria = weeklySoldCriteria.lte(criteria.getMaxWeeklySold());
            }
            query.addCriteria(weeklySoldCriteria);
        }

        if (criteria.getMinMonthlySold() != null || criteria.getMaxMonthlySold() != null) {
            Criteria monthlySoldCriteria = Criteria.where("sales.monthlySold");
            if (criteria.getMinMonthlySold() != null) {
                monthlySoldCriteria = monthlySoldCriteria.gte(criteria.getMinMonthlySold());
            }
            if (criteria.getMaxMonthlySold() != null) {
                monthlySoldCriteria = monthlySoldCriteria.lte(criteria.getMaxMonthlySold());
            }
            query.addCriteria(monthlySoldCriteria);
        }
        */

        if (StringUtils.hasText(criteria.getSortBy())) {
            Sort.Direction direction = "desc".equalsIgnoreCase(criteria.getSortDirection()) ?
                    Sort.Direction.DESC : Sort.Direction.ASC;

            String sortField = mapSortField(criteria.getSortBy());
            query.with(Sort.by(direction, sortField));
        } else {
            query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        // Pagination
        query.with(pageable);
        List<Product> products = mongoTemplate.find(query, Product.class);
        Long total = mongoTemplate.count(query.skip(0).limit(0), Product.class);

        // Convert to DTOs using mapstruct
        List<ProductDTO> bookDTOs = products.stream()
                .map(this::convertToDTO)
                .toList();

        log.debug("Optimized search returned {} results out of {} total", bookDTOs.size(), total);

        // Create and return Page object for response
        return new PageImpl<>(bookDTOs, pageable, total);
    }

    private String mapSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "price", "saleprice" -> "pricing.salePrice";
            case "rating" -> "rating.average";
            case "published", "publisheddate" -> "publishedDate";
            case "created", "createdat" -> "createdAt";
            case "stock", "stockquantity" -> "stockQuantity";
            case "sales", "totalsold" -> "sales.totalSold";
            case "dailysales", "dailysold" -> "sales.dailySold";
            case "weeklysales", "weeklysold" -> "sales.weeklySold";
            case "monthlysales", "monthlysold" -> "sales.monthlySold";
            default -> "title";
        };
    }

    // ===== Product OPERATIONS BY Supplier =====
    /**
     * Get products by Supplier
     */
    public List<ProductDTO> getProductsByAuthor(String authorId) {
        return getProductsByAuthor(authorId, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsByAuthor(String authorId, int limit) {
        log.debug("Getting {} products by Supplier: {}", limit, authorId);

        // Validate authorId
        if (!StringUtils.hasText(authorId)) {
            throw new IllegalArgumentException("Supplier ID is required");
        }

        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        // Call repository method
        List<Product> products = ProductRepository.findByAuthorId(authorId);

        // Convert to DTO list
        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    public List<ProductDTO> getProductsByAuthorName(String authorName) {
        return getProductsByAuthorName(authorName, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsByAuthorName(String authorName, int limit) {
        log.debug("Getting {} products by Supplier name: {}", limit, authorName);

        // Validate authorName
        if (!StringUtils.hasText(authorName)) {
            throw new IllegalArgumentException("Supplier name is required");
        }

        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        // Call repository method
        List<Product> products = ProductRepository.findByAuthorName(authorName);

        // Convert to DTO list
        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    // ===== Product OPERATIONS BY Manufacturer =====
    /**
     * Get products by Manufacturer
     */
    public List<ProductDTO> getProductsByPublisher(String publisherId) {
        return getProductsByPublisher(publisherId, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsByPublisher(String publisherId, int limit) {
        log.debug("Getting {} products by Manufacturer: {}", limit, publisherId);

        // Validate publisherId
        if (!StringUtils.hasText(publisherId)) {
            throw new IllegalArgumentException("Manufacturer ID is required");
        }

        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        // Call repository method
        List<Product> products = ProductRepository.findByPublisherId(publisherId);

        // Convert to DTO list
        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    public List<ProductDTO> getProductsByPublisherName(String publisherName) {
        return getProductsByPublisherName(publisherName, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsByPublisherName(String publisherName, int limit) {
        log.debug("Getting {} products by Manufacturer name: {}", limit, publisherName);

        // Validate publisherName
        if (!StringUtils.hasText(publisherName)) {
            throw new IllegalArgumentException("Manufacturer name is required");
        }

        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        // Call repository method
        List<Product> products = ProductRepository.findByPublisherName(publisherName);

        // Convert to DTO list
        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    // ===== Product OPERATIONS BY SERIES =====
    /**
     * Get products by series
     */
    public List<ProductDTO> getProductsBySeries(String seriesId) {
        return getProductsBySeries(seriesId, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsBySeries(String seriesId, int limit) {
        log.debug("Getting {} products by series: {}", limit, seriesId);

        // Validate seriesId
        if (!StringUtils.hasText(seriesId)) {
            throw new IllegalArgumentException("Series ID is required");
        }

        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        // Call repository method
        List<Product> products = ProductRepository.findBySeriesId(seriesId);

        // Convert to DTO list
        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    public List<ProductDTO> getProductsBySeriesName(String seriesName) {
        return getProductsBySeriesName(seriesName, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsBySeriesName(String seriesName, int limit) {
        log.debug("Getting {} products by series name: {}", limit, seriesName);

        // Validate seriesName
        if (!StringUtils.hasText(seriesName)) {
            throw new IllegalArgumentException("Series name is required");
        }

        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        // Call repository method
        List<Product> products = ProductRepository.findBySeriesName(seriesName);

        // Convert to DTO list
        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    // ===== CATEGORY OPERATIONS =====

    /**
     * Get products by category
     */
    public List<ProductDTO> getProductsByCategory(String categoryId) {
        return getProductsByCategory(categoryId, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsByCategory(String categoryId, int limit) {
        log.debug("Getting {} products by category: {}", limit, categoryId);

        // Validate categoryId
        if (!StringUtils.hasText(categoryId)) {
            throw new IllegalArgumentException("Category ID is required");
        }
        
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        // Call repository method
        List<Product> products = ProductRepository.findByCategoryId(categoryId);

        // Convert to DTO list
        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get products by multiple categories
     */
    public List<ProductDTO> getProductsInMultipleCategories(List<String> categoryIds) {
        log.debug("Getting products in categories: {}", categoryIds);

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Category IDs cannot be null or empty");
        }

        List<Product> products = ProductRepository.findByMultipleCategories(categoryIds);

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ===== PRICING OPERATIONS =====

    /**
     * Get products by price range
     */
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return getProductsByPriceRange(minPrice, maxPrice, 50); // Default limit of 50
    }

    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int limit) {
        log.debug("Getting {} products in price range: {} - {}", limit, minPrice, maxPrice);

        // Validation
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        List<Product> products = ProductRepository.findByPriceRange(
                minPrice != null ? minPrice : BigDecimal.ZERO,
                maxPrice != null ? maxPrice : new BigDecimal("9999999")
        );

        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get discounted products
     */
    public List<ProductDTO> getDiscountedProducts() {
        log.debug("Getting discounted products");

        List<Product> products = ProductRepository.findDiscountedProducts();

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ===== STOCK OPERATIONS =====

    /**
     * Update stock quantity
     */
    @Transactional
    public void updateStock(String bookId, Integer quantity) {
        log.info("Updating stock for Product: {} to quantity: {}", bookId, quantity);

        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Product Product = ProductRepository.findById(bookId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));

        Product.setStockQuantity(quantity);
        Product.setAvailability(quantity > 0 ? Availability.IN_STOCK : Availability.OUT_OF_STOCK);
        Product.setUpdatedAt(LocalDateTime.now());

        ProductRepository.save(Product);
        log.info("Successfully updated stock for Product: {}", bookId);
    }

    /**
     * Get low stock products
     */
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        log.debug("Getting low stock products with threshold: {}", threshold);

        if (threshold == null || threshold < 0) {
            throw new IllegalArgumentException("Threshold must be a positive number");
        }

        List<Product> products = ProductRepository.findLowStockProducts(threshold);

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ===== FEATURED OPERATIONS =====

    /**
     * Get new releases
     */
    public List<ProductDTO> getNewReleases() {
        return getNewReleases(20); // Default limit of 20
    }

    public List<ProductDTO> getNewReleases(int limit) {
        log.debug("Getting {} new releases", limit);
        
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        List<Product> products = ProductRepository.findNewReleases();

        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get bestsellers
     */
    public List<ProductDTO> getBestsellers() {
        return getBestsellers(20); // Default limit of 20
    }

    public List<ProductDTO> getBestsellers(int limit) {
        log.debug("Getting {} bestsellers", limit);
        
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        List<Product> products = ProductRepository.findBestsellers();

        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get daily bestsellers
     */
    public List<ProductDTO> getDailyBestsellers() {
        return getDailyBestsellers(20);
    }

    public List<ProductDTO> getDailyBestsellers(int limit) {
        log.debug("Getting {} daily bestsellers", limit);
        
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        List<Product> products = ProductRepository.findDailyBestsellers();

        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get weekly bestsellers
     */
    public List<ProductDTO> getWeeklyBestsellers() {
        return getWeeklyBestsellers(20);
    }

    public List<ProductDTO> getWeeklyBestsellers(int limit) {
        log.debug("Getting {} weekly bestsellers", limit);
        
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        List<Product> products = ProductRepository.findWeeklyBestsellers();

        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get monthly bestsellers
     */
    public List<ProductDTO> getMonthlyBestsellers() {
        return getMonthlyBestsellers(20);
    }

    public List<ProductDTO> getMonthlyBestsellers(int limit) {
        log.debug("Getting {} monthly bestsellers", limit);
        
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }

        List<Product> products = ProductRepository.findMonthlyBestsellers();

        return products.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    // ===== RECOMMENDATION OPERATIONS =====

    /**
     * Get recommended products
     */
    public List<ProductDTO> getRecommendedProducts() {
        log.debug("Getting recommended products");

        List<Product> products = ProductRepository.findRecommendedProducts();

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get similar products
     */
    public List<ProductDTO> getSimilarProducts(String bookId) {
        return getSimilarProducts(bookId, 10); // Default limit of 10
    }

    public List<ProductDTO> getSimilarProducts(String bookId, int limit) {
        log.debug("Getting {} similar products for: {}", limit, bookId);
        
        if (limit <= 0 || limit > 50) {
            throw new IllegalArgumentException("Limit must be between 1 and 50");
        }

        Product currentProduct = ProductRepository.findById(bookId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));

        List<String> categoryIds = currentProduct.getCategories().stream()
                .map(ProductCategory::getCategoryId)
                .toList();
        List<String> supplierNames = currentProduct.getSuppliers().stream()
                .map(supplier -> supplier.getName())
                .toList();

        List<Product> similarProducts = ProductRepository.findSimilarProducts(categoryIds, supplierNames, bookId);

        return similarProducts.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }


    // ====== RATING OPERATIONS =====

    /*
     * Add rating to a Product
     */
    @Transactional
    public ApiResponse addRating(String bookId, ProductRatingRequest request) {
        log.info("Adding rating: bookId={}, userId={}. rating={}", bookId, request.getUserId(), request.getRating());

        try {
            // Validate input
            if (!StringUtils.hasText(bookId)) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Product ID is required")
                        .build();
            }

            if (request.getRating() == null || request.getRating() < 1.0 || request.getRating() > 5.0) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Rating must be between 1 and 5")
                        .build();
            }

            // Find Product
            Product Product = ProductRepository.findById(bookId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));

            // Busines validation
            if (Product.getStatus() != ProductStatus.ACTIVE) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Cannot rate an inactive Product")
                        .build();
            }

            // Get or create Rating
            Rating rating = Product.getRating();
            if (rating == null) {
                rating = Rating.builder()
                        .average(0.0)
                        .count(0)
                        .distribution(new HashMap<>())
                        .percentages(new HashMap<>())
                        .build();
            }

            rating.addRating(request.getRating());
            Product.setRating(rating);
            Product.setUpdatedAt(LocalDateTime.now());
            ProductRepository.save(Product);

            log.info("Raring added successfully: bookId={}, userId={}. New average: {}, Total ratings: {}",
                    bookId, request.getUserId(), rating.getAverage(), rating.getCount());

            return ApiResponse.builder()
                    .success(true)
                    .message("Rating added successfully")
                    .data(Map.of(
                            "newAverage", rating.getAverage(),
                            "totalRatings", rating.getCount(),
                            "userRating", request.getRating()
                    ))
                    .build();
        } catch (ProductNotFoundException e) {
            log.warn("Product not found with ID: {}", bookId);
            throw e; // Re-throw to match test expectations
        } catch (Exception e) {
            log.error("Error adding rating: bookId={}", bookId, e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Failed to add rating: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Get rating stats for a Product
     */
    public RatingStatsDTO getRatingStats(String bookId) {
        log.debug("Getting rating stats for bookId: {}", bookId);

        Product Product = ProductRepository.findById(bookId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + bookId));

        Rating rating = Product.getRating();
        if (rating == null) {
            return RatingStatsDTO.builder()
                    .averageRating(0.0)
                    .totalRatings(0)
                    .fiveStar(0)
                    .fourStar(0)
                    .threeStar(0)
                    .twoStar(0)
                    .oneStar(0)
                    .build();
        }

        return RatingStatsDTO.builder()
                .averageRating(rating.getAverage())
                .totalRatings(rating.getCount())
                .fiveStar(rating.getDistribution().getOrDefault("5", 0))
                .fourStar(rating.getDistribution().getOrDefault("4", 0))
                .threeStar(rating.getDistribution().getOrDefault("3", 0))
                .twoStar(rating.getDistribution().getOrDefault("2", 0))
                .oneStar(rating.getDistribution().getOrDefault("1", 0))
                .percentageFiveStar(rating.getPercentages().getOrDefault("5", 0.0))
                .percentageFourStar(rating.getPercentages().getOrDefault("4", 0.0))
                .percentageThreeStar(rating.getPercentages().getOrDefault("3", 0.0))
                .percentageTwoStar(rating.getPercentages().getOrDefault("2", 0.0))
                .percentageOneStar(rating.getPercentages().getOrDefault("1", 0.0))
                .build();
    }


    // ===== HELPER METHODS =====

    /**
     * Convert Product entity to ProductDTO
     */
    private ProductDTO convertToDTO(Product Product) {
        return ProductMapper.toDTO(Product);
    }

    /**
     * Convert ProductDTO to Product entity
     */
    private Product convertToEntity(ProductDTO dto) {
        return ProductMapper.toEntity(dto);
    }

    /**
     * Validate Product data
     */
    private void validateBookData(ProductDTO ProductDTO) {
        if (!StringUtils.hasText(ProductDTO.getTitle())) {
            throw new IllegalArgumentException("Product title is required");
        }

        if (!StringUtils.hasText(ProductDTO.getSku())) {
            throw new IllegalArgumentException("Product SKU is required");
        }

        if (StringUtils.hasText(ProductDTO.getSku())) {
            String sku = ProductDTO.getSku().trim();
            if (sku.length() > 50) {
                throw new IllegalArgumentException("SKU cannot exceed 50 characters");
            }
            if (!sku.matches("^[A-Z0-9\\-_]+$")) {
                throw new IllegalArgumentException("SKU must contain only uppercase letters, numbers, hyphens and underscores");
            }
        }

        if (ProductDTO.getStockQuantity() != null && ProductDTO.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        if (ProductDTO.getPricing() != null) {
            if (ProductDTO.getPricing().getListPrice() != null &&
                    ProductDTO.getPricing().getListPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("List price cannot be negative");
            }
            if (ProductDTO.getPricing().getSalePrice() != null &&
                    ProductDTO.getPricing().getSalePrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Sale price cannot be negative");
            }
        }

        if (ProductDTO.getSuppliers() != null && !ProductDTO.getSuppliers().isEmpty()) {
            for (Supplier supplier : ProductDTO.getSuppliers()) {
                if (!StringUtils.hasText(supplier.getName())) {
                    throw new IllegalArgumentException("Supplier name is required");
                }
            }
        }

        if (ProductDTO.getCategories() != null && !ProductDTO.getCategories().isEmpty()) {
            for (ProductCategory category : ProductDTO.getCategories()) {
                if (!StringUtils.hasText(category.getCategoryId())) {
                    throw new IllegalArgumentException("Category ID is required");
                }
                if (!StringUtils.hasText(category.getName())) {
                    throw new IllegalArgumentException("Category name is required");
                }
            }
        }

        if (ProductDTO.getManufacturer() != null) {
            if (!StringUtils.hasText(ProductDTO.getManufacturer().getManufacturerId())) {
                throw new IllegalArgumentException("Manufacturer ID is required");
            }
            if (!StringUtils.hasText(ProductDTO.getManufacturer().getName())) {
                throw new IllegalArgumentException("Manufacturer name is required");
            }
        }
    }

    // ===== RATING OPERATIONS - TODO: IMPLEMENT FROM SCRATCH =====
    
    /*
     * TODO: Implement addRating method
     * @Transactional
     * public ApiResponse addRating(String bookId, ProductRatingRequest request) {
     *     // Your implementation here
     *     return null;
     * }
     */
     
    /*
     * TODO: Implement getRatingStats method  
     * public RatingStatsDTO getRatingStats(String bookId) {
     *     // Your implementation here
     *     return null;
     * }
     */

    /**
     * Check if user can rate this Product
     */
    public boolean canUserRate(String productId, String userId) {
        // For now, simple check - can be enhanced with purchase verification
        Product product = ProductRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
        
        return product.getStatus() == ProductStatus.ACTIVE;
    }

    // ===== SUPPLIER OPERATIONS =====
    
    @Override
    public List<ProductDTO> getProductsBySupplier(String supplierId, int limit) {
        log.debug("Getting products by supplier ID: {} with limit: {}", supplierId, limit);
        // Implementation using MongoDB query
        Query query = new Query(Criteria.where("suppliers.supplierId").is(supplierId)
                .and("status").is(ProductStatus.ACTIVE));
        query.limit(limit);
        
        List<Product> products = mongoTemplate.find(query, Product.class);
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Override
    public List<ProductDTO> getProductsBySupplierName(String supplierName, int limit) {
        log.debug("Getting products by supplier name: {} with limit: {}", supplierName, limit);
        Query query = new Query(Criteria.where("suppliers.name").regex(supplierName, "i")
                .and("status").is(ProductStatus.ACTIVE));
        query.limit(limit);
        
        List<Product> products = mongoTemplate.find(query, Product.class);
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ===== MANUFACTURER OPERATIONS =====
    
    @Override
    public List<ProductDTO> getProductsByManufacturer(String manufacturerId, int limit) {
        log.debug("Getting products by manufacturer ID: {} with limit: {}", manufacturerId, limit);
        Query query = new Query(Criteria.where("manufacturer.manufacturerId").is(manufacturerId)
                .and("status").is(ProductStatus.ACTIVE));
        query.limit(limit);
        
        List<Product> products = mongoTemplate.find(query, Product.class);
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Override
    public List<ProductDTO> getProductsByManufacturerName(String manufacturerName, int limit) {
        log.debug("Getting products by manufacturer name: {} with limit: {}", manufacturerName, limit);
        Query query = new Query(Criteria.where("manufacturer.name").regex(manufacturerName, "i")
                .and("status").is(ProductStatus.ACTIVE));
        query.limit(limit);
        
        List<Product> products = mongoTemplate.find(query, Product.class);
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ===== TODO: BẠN TỰ THÊM CÁC METHODS KHÁC Ở ĐÂY =====
    
}

