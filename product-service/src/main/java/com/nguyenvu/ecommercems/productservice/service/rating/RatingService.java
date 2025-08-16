package com.nguyenvu.ecommercems.productservice.service.rating;

import com.nguyenvu.ecommercems.productservice.dto.ApiResponse;
import com.nguyenvu.ecommercems.productservice.dto.ProductRatingRequest;
import com.nguyenvu.ecommercems.productservice.dto.RatingStatsDTO;
import com.nguyenvu.ecommercems.productservice.exception.ProductNotFoundException;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.embedded.Rating;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductStatus;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import com.nguyenvu.ecommercems.productservice.service.shared.cache.ProductCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for handling Product ratings
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final ProductRepository productRepository;
    private final ProductCacheService cacheService;

    public ApiResponse addRating(String productId, ProductRatingRequest request) {
        log.info("Add rating for product: {}", productId);

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));

            if (product.getStatus() != ProductStatus.ACTIVE) {
                return ApiResponse.error("Product is not active", "Cannot add rating to inactive product");
            }

            updateProductRating(product, request.getRating());

            productRepository.save(product);
            cacheService.evictById(productId);

            return ApiResponse.success("Rating added successfully", product.getRating());

        } catch (Exception e) {
            log.error("Error adding rating for product {}: {}", productId, e.getMessage());
            return ApiResponse.error("Failed to add rating", e.getMessage());
        }
    }

    public RatingStatsDTO getRatingStats(String productId) {
        log.info("Get rating stats for product: {}", productId);

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));

            Rating rating = product.getRating();

            return RatingStatsDTO.builder()
                    .averageRating(rating.getAverage())
                    .totalRatings(rating.getCount())
                    .build();

        } catch (Exception e) {
            log.error("Error getting rating stats for product {}: {}", productId, e.getMessage());
            return RatingStatsDTO.builder()
                    .averageRating(0)
                    .totalRatings(0)
                    .build();
        }
    }

    private void updateProductRating(Product product, Double newRating) {
        Rating currentRating = product.getRating();

        if (currentRating == null) {
            // First rating
            currentRating = Rating.builder()
                    .average(newRating)
                    .count(1)
                    .build();
        } else {
            // Calculate new average
            int newCount = currentRating.getCount() + 1;
            double newAverage = ((currentRating.getAverage() * currentRating.getCount()) + newRating) / newCount;

            currentRating.setCount(newCount);
            currentRating.setAverage(newAverage);
        }

        product.setRating(currentRating);
    }
}
