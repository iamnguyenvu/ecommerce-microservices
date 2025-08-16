package com.nguyenvu.ecommercems.productservice.controller;

import com.nguyenvu.ecommercems.productservice.dto.*;
import com.nguyenvu.ecommercems.productservice.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "products", description = "Product management operations")
public class ProductController {
    
    private final ProductService productService;
    
    // ===== BASIC CRUD ENDPOINTS =====
    
    /**
     * Get all products with pagination
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products with pagination")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("GET /api/v1/products - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.getAllProducts(pageable);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Get Product by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Product by ID", description = "Retrieve a specific Product by its ID")
    public ResponseEntity<ProductDTO> getBookById(
            @Parameter(description = "Product ID", required = true)
            @PathVariable String id) {
        
        log.info("GET /api/v1/products/{}", id);
        
        ProductDTO Product = productService.getProductById(id);
        return ResponseEntity.ok(Product);
    }

    /**
     * Get Product by code
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get Product by code", description = "Retrieve a Product by its unique code")
    public ResponseEntity<ProductDTO> getBookByCode(
            @Parameter(description = "Product code", required = true)
            @PathVariable String code) {
        
        log.info("GET /api/v1/products/code/{}", code);
        
        ProductDTO Product = productService.getProductByCode(code);
        return ResponseEntity.ok(Product);
    }

    /**
     * Get Product by ISBN
     */
    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Get Product by ISBN", description = "Retrieve a Product by its ISBN")
    public ResponseEntity<ProductDTO> getBookByIsbn(
            @Parameter(description = "Product ISBN", required = true)
            @PathVariable String isbn) {
        
        log.info("GET /api/v1/products/sku/{}", isbn);
        
        ProductDTO Product = productService.getProductBySku(isbn);
        return ResponseEntity.ok(Product);
    }

    /**
     * Create new Product
     */
    @PostMapping
    @Operation(summary = "Create new Product", description = "Create a new Product in the system")
    public ResponseEntity<ProductDTO> createBook(
            @Valid @RequestBody ProductDTO ProductDTO) {
        
        log.info("POST /api/v1/products - title: {}", ProductDTO.getTitle());
        
        ProductDTO savedProduct = productService.saveProduct(ProductDTO);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * Update Product
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Product", description = "Update an existing Product")
    public ResponseEntity<ProductDTO> updateBook(
            @Parameter(description = "Product ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody ProductDTO ProductDTO) {
        
        log.info("PUT /api/v1/products/{} - title: {}", id, ProductDTO.getTitle());
        
        ProductDTO updatedProduct = productService.updateProduct(id, ProductDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete Product (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Product", description = "Soft delete a Product (sets status to INACTIVE)")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Product ID", required = true)
            @PathVariable String id) {
        
        log.info("DELETE /api/v1/products/{}", id);
        
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ===== SEARCH ENDPOINTS =====
    
    /**
     * Simple text search
     */
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by text in title, description, or Suppliers")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Search text", required = true)
            @RequestParam String q) {
        
        log.info("GET /api/v1/products/search?q={}", q);
        
        List<ProductDTO> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    /**
     * Advanced search with filters
     */
    @PostMapping("/search")
    @Operation(summary = "Advanced search", description = "Advanced search with multiple filters")
    public ResponseEntity<Page<ProductDTO>> advancedSearch(
            @Valid @RequestBody ProductSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("POST /api/v1/products/search - criteria: {}", criteria);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products = productService.searchProductsWithFilters(criteria, pageable);
        
        return ResponseEntity.ok(products);
    }

    // ===== CATEGORY ENDPOINTS =====
    
    /**
     * Get products by category
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve products in a specific category")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "50") int limit) {
        
        log.info("GET /api/v1/products/category/{} - limit: {}", categoryId, limit);
        
        List<ProductDTO> products = productService.getProductsByCategory(categoryId, limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by multiple categories
     */
    @PostMapping("/categories")
    @Operation(summary = "Get products by multiple categories", description = "Retrieve products in multiple categories")
    public ResponseEntity<List<ProductDTO>> getProductsByCategories(
            @RequestBody List<String> categoryIds) {
        
        log.info("POST /api/v1/products/categories - categories: {}", categoryIds);
        
        List<ProductDTO> products = productService.getProductsInMultipleCategories(categoryIds);
        return ResponseEntity.ok(products);
    }

    // ===== PRICING ENDPOINTS =====
    
    /**
     * Get products by price range
     */
    @GetMapping("/price-range")
    @Operation(summary = "Get products by price range", description = "Retrieve products within a price range")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "50") int limit) {
        
        log.info("GET /api/v1/products/price-range?minPrice={}&maxPrice={}&limit={}", minPrice, maxPrice, limit);
        
        List<ProductDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice, limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get discounted products
     */
    @GetMapping("/discounted")
    @Operation(summary = "Get discounted products", description = "Retrieve products with discounts")
    public ResponseEntity<List<ProductDTO>> getDiscountedProducts() {
        
        log.info("GET /api/v1/products/discounted");
        
        List<ProductDTO> products = productService.getDiscountedProducts();
        return ResponseEntity.ok(products);
    }

    // ===== INVENTORY ENDPOINTS =====
    
    /**
     * Update stock quantity
     */
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update stock", description = "Update Product stock quantity")
    public ResponseEntity<Void> updateStock(
            @Parameter(description = "Product ID", required = true)
            @PathVariable String id,
            @Parameter(description = "New stock quantity", required = true)
            @RequestParam Integer quantity) {
        
        log.info("PATCH /api/v1/products/{}/stock - quantity: {}", id, quantity);
        
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Get low stock products
     */
    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products", description = "Retrieve products with low stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        
        log.info("GET /api/v1/products/low-stock?threshold={}", threshold);
        
        List<ProductDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    // ===== FEATURED ENDPOINTS =====
    
    /**
     * Get new releases
     */
    @GetMapping("/new-releases")
    @Operation(summary = "Get new releases", description = "Retrieve recently released products")
    public ResponseEntity<List<ProductDTO>> getNewReleases(
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("GET /api/v1/products/new-releases?limit={}", limit);
        
        List<ProductDTO> products = productService.getNewReleases(limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get bestsellers
     */
    @GetMapping("/bestsellers")
    @Operation(summary = "Get bestsellers", description = "Retrieve best-selling products")
    public ResponseEntity<List<ProductDTO>> getBestsellers(
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("GET /api/v1/products/bestsellers?limit={}", limit);
        
        List<ProductDTO> products = productService.getBestsellers(limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get daily bestsellers
     */
    @GetMapping("/bestsellers/daily")
    @Operation(summary = "Get daily bestsellers", description = "Retrieve best-selling products for today")
    public ResponseEntity<List<ProductDTO>> getDailyBestsellers(
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("GET /api/v1/products/bestsellers/daily?limit={}", limit);
        
        List<ProductDTO> products = productService.getDailyBestsellers(limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get weekly bestsellers
     */
    @GetMapping("/bestsellers/weekly")
    @Operation(summary = "Get weekly bestsellers", description = "Retrieve best-selling products for this week")
    public ResponseEntity<List<ProductDTO>> getWeeklyBestsellers(
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("GET /api/v1/products/bestsellers/weekly?limit={}", limit);
        
        List<ProductDTO> products = productService.getWeeklyBestsellers(limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get monthly bestsellers
     */
    @GetMapping("/bestsellers/monthly")
    @Operation(summary = "Get monthly bestsellers", description = "Retrieve best-selling products for this month")
    public ResponseEntity<List<ProductDTO>> getMonthlyBestsellers(
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("GET /api/v1/products/bestsellers/monthly?limit={}", limit);
        
        List<ProductDTO> products = productService.getMonthlyBestsellers(limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get recommended products
     */
    @GetMapping("/recommended")
    @Operation(summary = "Get recommended products", description = "Retrieve recommended products")
    public ResponseEntity<List<ProductDTO>> getRecommendedProducts() {
        
        log.info("GET /api/v1/products/recommended");
        
        List<ProductDTO> products = productService.getRecommendedProducts();
        return ResponseEntity.ok(products);
    }

    // ===== RECOMMENDATION ENDPOINTS =====
    
    /**
     * Get similar products
     */
    @GetMapping("/{id}/similar")
    @Operation(summary = "Get similar products", description = "Retrieve products similar to the specified Product")
    public ResponseEntity<List<ProductDTO>> getSimilarProducts(
            @Parameter(description = "Product ID", required = true)
            @PathVariable String id,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("GET /api/v1/products/{}/similar?limit={}", id, limit);
        
        List<ProductDTO> products = productService.getSimilarProducts(id, limit);
        return ResponseEntity.ok(products);
    }

    // ===== UTILITY ENDPOINTS =====
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the Product service is running")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("GET /api/v1/products/health");
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "Product-service",
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(health);
    }

    /**
     * Validate Product code endpoint (useful for frontend validation)
     */
    @GetMapping("/validate/code/{code}")
    @Operation(summary = "Validate Product code", description = "Check if Product code is available")
    public ResponseEntity<Map<String, Object>> validateBookCode(
            @Parameter(description = "Product code to validate", required = true)
            @PathVariable String code) {
        
        log.info("GET /api/v1/products/validate/code/{}", code);
        
        try {
            ProductDTO existingProduct = productService.getProductByCode(code);
            // If we get here, Product exists
            Map<String, Object> response = Map.of(
                "available", false,
                "message", "Product code already exists",
                "existingProductId", existingProduct.getId()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Product doesn't exist, code is available
            Map<String, Object> response = Map.of(
                "available", true,
                "message", "Product code is available"
            );
            return ResponseEntity.ok(response);
        }
    }

    // ===== Supplier ENDPOINTS =====

    /**
     * Get products by Supplier ID
     */
    @GetMapping("/Suppliers/id/{authorId}")
    @Operation(summary = "Get products by Supplier ID", description = "Retrieve products written by a specific Supplier")
    public ResponseEntity<List<ProductDTO>> getProductsByAuthorId(
            @Parameter(description = "Supplier ID", required = true)
            @PathVariable String authorId,
            @RequestParam(defaultValue = "50") int limit) {
        log.info("GET /api/v1/products/Suppliers/id/{} - limit: {}", authorId, limit);

        List<ProductDTO> products = productService.getProductsBySupplier(authorId, limit);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

    /**
     * Get products by Supplier name
     */
    @GetMapping("/Suppliers/name/{authorName}")
    @Operation(summary = "Get products by Supplier name", description = "Retrieve products written by a specific Supplier")
    public ResponseEntity<List<ProductDTO>> getProductsByAuthorName(
            @Parameter(description = "Supplier name", required = true)
            @PathVariable String authorName,
            @RequestParam(defaultValue = "50") int limit) {
        log.info("GET /api/v1/products/Suppliers/name/{} - limit: {}", authorName, limit);

        List<ProductDTO> products = productService.getProductsBySupplierName(authorName, limit);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

    // ===== Manufacturer ENDPOINTS =====

    /**
     * Get products by Manufacturer ID
     */
    @GetMapping("/publishers/id/{publisherId}")
    @Operation(summary = "Get products by Manufacturer ID", description = "Retrieve products published by a specific Manufacturer")
    public ResponseEntity<List<ProductDTO>> getProductsByPublisherId(
            @Parameter(description = "Manufacturer ID", required = true)
            @PathVariable String publisherId,
            @RequestParam(defaultValue = "50") int limit) {

        log.info("GET /api/v1/products/manufacturers/id/{} - limit: {}", publisherId, limit);

        List<ProductDTO> products = productService.getProductsByManufacturer(publisherId, limit);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

    /**
     * Get products by Manufacturer name
     */
    @GetMapping("/publishers/name/{publisherName}")
    @Operation(summary = "Get products by Manufacturer name", description = "Retrieve products published by a specific Manufacturer")
    public ResponseEntity<List<ProductDTO>> getProductsByPublisherName(
            @Parameter(description = "Manufacturer name", required = true)
            @PathVariable String publisherName,
            @RequestParam(defaultValue = "50") int limit) {

        log.info("GET /api/v1/products/manufacturers/name/{} - limit: {}", publisherName, limit);

        List<ProductDTO> products = productService.getProductsByManufacturerName(publisherName, limit);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

    // ===== SERIES ENDPOINTS =====

    /**
     * Get products by series ID
     */
    @GetMapping("/series/{seriesId}")
    @Operation(summary = "Get products by series", description = "Retrieve products in a specific series")
    public ResponseEntity<List<ProductDTO>> getProductsBySeriesId(
            @Parameter(description = "Series ID", required = true)
            @PathVariable String seriesId,
            @RequestParam(defaultValue = "50") int limit) {

        log.info("GET /api/v1/products/series/{} - limit: {}", seriesId, limit);

        List<ProductDTO> products = productService.getProductsBySeries(seriesId, limit);
        
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(products);
    }

    // , Statistics endpoints, Admin endpoints, etc.

    // ====== RATING ENDPOINTS =====

    // TODO: IMPLEMENT RATING METHODS - START FROM HERE
    // Rating methods removed for clean implementation
    // Follow RATING_FROM_SCRATCH.md guide
    
    /*
     * TODO: Add addRating method here
     * - POST /{bookId}/ratings
     * - Validation: 1-5 stars
     * - Security: userId from header
     * - Call service.addRating()
     */

    /*
     * TODO: Add getRatingStats method here  
     * - GET /{bookId}/ratings/stats
     * - Return rating statistics
     * - Call service.getRatingStats()
     */

    /**
     * Check if user can rate this Product (has purchased it)
     */
    @GetMapping("/{bookId}/ratings/can-rate")
    @Operation(summary = "Check rating eligibility", description = "Check if user can rate this Product")
    public ResponseEntity<ApiResponse> canUserRate(
            @PathVariable String bookId,
            @RequestHeader(value = "X-User-ID", required = true) String userId
    ) {
        log.info("GET /api/v1/products/{}/ratings/can-rate - userId: {}", bookId, userId);
        
        try {
            boolean canRate = productService.canUserRate(bookId, userId);
            
            ApiResponse response = ApiResponse.builder()
                    .success(true)
                    .message(canRate ? "User can rate this Product" : "User cannot rate this Product")
                    .data(Map.of("canRate", canRate))
                    .build();
                    
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error checking rating eligibility for Product: {} by user: {}", bookId, userId, e);
            
            ApiResponse errorResponse = ApiResponse.builder()
                    .success(false)
                    .message("Không thể kiểm tra quyền đánh giá: " + e.getMessage())
                    .build();
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ===== RATING ENDPOINTS =====

    @PostMapping("/{bookId}/ratings")
    @Operation(summary = "Get Product ratings",
            description = "Add a 1-5 star rating for a Product with optional review")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ratign added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid rating value"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse> addRating(
            @Parameter(description = "Product ID", required = true)
            @PathVariable String bookId,
            @Valid @RequestBody ProductRatingRequest request,
            @Parameter(description = "User ID from request header", required = true)
            @RequestHeader(value = "X-User-ID", required = true) String userId
    ) {
        log.info("POST /api/v1/products/{}/ratings - userId: {}, rating: {}", bookId, userId, request.getRating());

        try {
            if (request.getRating() < 1 || request.getRating() > 5) {
                return ResponseEntity.badRequest().body(ApiResponse.builder()
                        .success(false)
                        .message("Đánh giá phải từ 1 đến 5 sao")
                        .build());
            }

            request.setUserId(userId);
            ApiResponse response = productService.addRating(bookId, request);

            if (response.isSuccess()) {
                log.info("Rating added successfully: bookId={}, userId={}, rating={}",
                        bookId, userId, request.getRating());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                log.warn("Failed to add rating: bookId={}, userId={}, message={}",
                        bookId, userId, response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            log.error("Error adding rating for Product: {} by user: {}", bookId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Unable to add rating: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get rating statistics for a Product
     */
    @GetMapping("/{bookId}/ratings/stats")
    @Operation(summary = "Get Product rating statistics",
            description = "Retrieve rating statistics for a Product including average rating, total ratings, and distribution")
    public ResponseEntity<ApiResponse> getRatingStats(@PathVariable String bookId) {
        log.info("GET /api/v1/products/{}/ratings/stats", bookId);

        try {
            RatingStatsDTO stats = productService.getRatingStats(bookId);

            ApiResponse response = ApiResponse.builder()
                    .success(true)
                    .message("Rating statistics retrieved successfully")
                    .data(Map.of("stats", stats))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting rating stats for Product: {}", bookId, e);

            ApiResponse errorResponse = ApiResponse.builder()
                    .success(false)
                    .message("Unable to retrive rating statistics: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
}

