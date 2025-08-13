package com.nguyenvu.ecommercems.productservice.service.Product.impl;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.ProductSearchCriteria;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.enums.Availability;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductStatus;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.Product.api.ProductSearchService;
import com.nguyenvu.ecommercems.productservice.service.Product.base.AbstractProductservice;
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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductsearchServiceImpl extends AbstractProductservice implements ProductSearchService {
    private final ProductRepository ProductRepository;
    private final ProductMapper ProductMapper;
    private final MongoTemplate mongoTemplate;

    // ===== SEARCH OPERATIONS =====

    /**
     * Search products by text
     */
    @Override
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

    @Override
    public Page<ProductDTO> searchProducts(String searchText, Pageable pageable) {
        if (!StringUtils.hasText(searchText)) {
            return Page.empty(pageable);
        }

        List<Product> products = ProductRepository.searchByTitleAndAuthor(searchText);
        List<ProductDTO> bookDTOs = products.stream()
                .map(ProductMapper::toDTO)
                .toList();

        return new PageImpl<>(bookDTOs, pageable, products.size());
    }

    /**
     * Advanced search with criteria
     */
    @Override
    public Page<ProductDTO> advancedSearch(ProductSearchCriteria criteria, Pageable pageable) {
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
        if (StringUtils.hasText(criteria.getAuthorName())) {
            query.addCriteria(Criteria.where("Suppliers.name").regex(criteria.getAuthorName(), "i"));
        }

        // Multiple Suppliers filter
        if (criteria.getAuthorNames() != null && !criteria.getAuthorNames().isEmpty()) {
            query.addCriteria(Criteria.where("Suppliers.name").in(criteria.getAuthorNames()));
        }

        // Publisher filter
        if (StringUtils.hasText(criteria.getPublisherId())) {
            query.addCriteria(Criteria.where("publisher.publisherId").is(criteria.getPublisherId()));
        }

        // Publisher name filter
        if (StringUtils.hasText(criteria.getPublisherName())) {
            query.addCriteria(Criteria.where("publisher.name").regex(criteria.getPublisherName(), "i"));
        }

        // Availability filter
        if (criteria.getAvailability() != null) {
            query.addCriteria(Criteria.where("availability").is(criteria.getAvailability()));
        }

        // Physical attributes filter
        if (criteria.getFormat() != null) {
            query.addCriteria(Criteria.where("physical.format").is(criteria.getFormat()));
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

        // Language filter
        if (StringUtils.hasText(criteria.getLanguage())) {
            query.addCriteria(Criteria.where("physical.language").regex(criteria.getLanguage(), "i"));
        }

        // Series filter
        if (StringUtils.hasText(criteria.getSeriesId())) {
            query.addCriteria(Criteria.where("series.seriesId").is(criteria.getSeriesId()));
        }

        // Sales filters
        if (criteria.getMinTotalSold() != null || criteria.getMaxTotalSold() != null) {
            Criteria totalSoldCriteria = Criteria.where("sales.totalSold");
            if (criteria.getMinTotalSold() != null) {
                totalSoldCriteria = totalSoldCriteria.gte(criteria.getMinTotalSold());
            }
            if (criteria.getMaxTotalSold() != null) {
                totalSoldCriteria = totalSoldCriteria.lte(criteria.getMaxTotalSold());
            }
            query.addCriteria(totalSoldCriteria);
        }

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

    @Override
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

    @Override
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

    // ===== Product OPERATIONS BY PUBLISHER =====
    /**
     * Get products by publisher
     */
    public List<ProductDTO> getProductsByPublisher(String publisherId) {
        return getProductsByPublisher(publisherId, 50); // Default limit of 50
    }

    @Override
    public List<ProductDTO> getProductsByPublisher(String publisherId, int limit) {
        log.debug("Getting {} products by publisher: {}", limit, publisherId);

        // Validate publisherId
        if (!StringUtils.hasText(publisherId)) {
            throw new IllegalArgumentException("Publisher ID is required");
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

    @Override
    public List<ProductDTO> getProductsByPublisherName(String publisherName, int limit) {
        log.debug("Getting {} products by publisher name: {}", limit, publisherName);

        // Validate publisherName
        if (!StringUtils.hasText(publisherName)) {
            throw new IllegalArgumentException("Publisher name is required");
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

    @Override
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

    @Override
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

    @Override
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
    @Override
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

    @Override
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
    @Override
    public List<ProductDTO> getDiscountedProducts() {
        log.debug("Getting discounted products");

        List<Product> products = ProductRepository.findDiscountedProducts();

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
