package com.nguyenvu.ecommercems.productservice.service;

import com.nguyenvu.ecommercems.productservice.dto.ApiResponse;
import com.nguyenvu.ecommercems.productservice.dto.ProductRatingRequest;
import com.nguyenvu.ecommercems.productservice.exception.ProductNotFoundException;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.model.embedded.Rating;
import com.nguyenvu.ecommercems.productservice.model.enums.ProductStatus;
import com.nguyenvu.ecommercems.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for ProductService Rating functionality
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Rating Tests")
class ProductServiceRatingTest {

    @Mock
    private ProductRepository ProductRepository;

    @InjectMocks
    private ProductserviceImpl ProductService;

    private Product testBook;
    private ProductRatingRequest validRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("BOOK001")
                .title("Test Product")
                .status(ProductStatus.ACTIVE)
                .rating(null) // No rating initially
                .build();

        validRequest = ProductRatingRequest.builder()
                .rating(4.5)
                .userId("USER001")
                .username("Test User")
                .review("Great Product!")
                .build();
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("Should reject null bookId")
        void shouldRejectNullBookId() {
            // When
            ApiResponse response = ProductService.addRating(null, validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Product ID is required");
            verify(ProductRepository, never()).findById(any());
            verify(ProductRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject empty bookId")
        void shouldRejectEmptyBookId() {
            // When
            ApiResponse response = ProductService.addRating("", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Product ID is required");
            verify(ProductRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should reject blank bookId")
        void shouldRejectBlankBookId() {
            // When
            ApiResponse response = ProductService.addRating("   ", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Product ID is required");
        }

        @Test
        @DisplayName("Should reject null rating")
        void shouldRejectNullRating() {
            // Given
            validRequest.setRating(null);

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Rating must be between 1 and 5");
            verify(ProductRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should reject rating below 1.0")
        void shouldRejectRatingBelowOne() {
            // Given
            validRequest.setRating(0.5);

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Rating must be between 1 and 5");
        }

        @Test
        @DisplayName("Should reject rating above 5.0")
        void shouldRejectRatingAboveFive() {
            // Given
            validRequest.setRating(5.1);

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Rating must be between 1 and 5");
        }

        @Test
        @DisplayName("Should accept boundary values 1.0 and 5.0")
        void shouldAcceptBoundaryValues() {
            // Given
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // Test 1.0
            validRequest.setRating(1.0);
            ApiResponse response1 = ProductService.addRating("BOOK001", validRequest);
            assertThat(response1.isSuccess()).isTrue();

            // Test 5.0
            validRequest.setRating(5.0);
            ApiResponse response2 = ProductService.addRating("BOOK001", validRequest);
            assertThat(response2.isSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("Product Validation Tests")
    class ProductValidationTests {

        @Test
        @DisplayName("Should handle Product not found")
        void shouldHandleBookNotFound() {
            // Given
            when(ProductRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ProductNotFoundException.class, () -> {
                ProductService.addRating("NONEXISTENT", validRequest);
            });

            verify(ProductRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject rating for inactive Product")
        void shouldRejectRatingForInactiveBook() {
            // Given
            testBook.setStatus(ProductStatus.INACTIVE);
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Cannot rate an inactive Product");
            verify(ProductRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reject rating for discontinued Product")
        void shouldRejectRatingForDiscontinuedBook() {
            // Given
            testBook.setStatus(ProductStatus.DISCONTINUED);
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo("Cannot rate an inactive Product");
        }

        @Test
        @DisplayName("Should accept rating for active Product")
        void shouldAcceptRatingForActiveBook() {
            // Given
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isTrue();
            verify(ProductRepository, times(1)).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Rating Creation Tests")
    class RatingCreationTests {

        @Test
        @DisplayName("Should create new Rating when Product has no rating")
        void shouldCreateNewRatingWhenBookHasNoRating() {
            // Given
            testBook.setRating(null);
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isTrue();

            // Capture the saved Product
            ArgumentCaptor<Product> bookCaptor = ArgumentCaptor.forClass(Product.class);
            verify(ProductRepository).save(bookCaptor.capture());
            Product savedProduct = bookCaptor.getValue();

            // Verify rating was created and initialized
            assertThat(savedBook.getRating()).isNotNull();
            assertThat(savedBook.getRating().getAverage()).isEqualTo(4.5);
            assertThat(savedBook.getRating().getCount()).isEqualTo(1);
            assertThat(savedBook.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should add to existing Rating when Product already has ratings")
        void shouldAddToExistingRatingWhenBookAlreadyHasRatings() {
            // Given - Product with existing rating
            Rating existingRating = Rating.builder()
                    .average(4.0)
                    .count(1)
                    .distribution(new HashMap<>())
                    .percentages(new HashMap<>())
                    .build();
            existingRating.getDistribution().put("4", 1);
            
            testBook.setRating(existingRating);
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // When - Add second rating
            validRequest.setRating(5.0);
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isTrue();

            // Capture the saved Product
            ArgumentCaptor<Product> bookCaptor = ArgumentCaptor.forClass(Product.class);
            verify(ProductRepository).save(bookCaptor.capture());
            Product savedProduct = bookCaptor.getValue();

            // Verify rating was updated correctly
            assertThat(savedBook.getRating().getCount()).isEqualTo(2);
            assertThat(savedBook.getRating().getAverage()).isEqualTo(4.5); // (4+5)/2 = 4.5
        }

        @Test
        @DisplayName("Should update updatedAt timestamp")
        void shouldUpdateUpdatedAtTimestamp() {
            // Given
            LocalDateTime beforeCall = LocalDateTime.now();
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // When
            ProductService.addRating("BOOK001", validRequest);

            // Then
            ArgumentCaptor<Product> bookCaptor = ArgumentCaptor.forClass(Product.class);
            verify(ProductRepository).save(bookCaptor.capture());
            Product savedProduct = bookCaptor.getValue();

            assertThat(savedBook.getUpdatedAt()).isNotNull();
            assertThat(savedBook.getUpdatedAt()).isAfter(beforeCall);
        }
    }

    @Nested
    @DisplayName("Response Data Tests")
    class ResponseDataTests {

        @Test
        @DisplayName("Should return correct response data")
        void shouldReturnCorrectResponseData() {
            // Given
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Rating added successfully");
            assertThat(response.getData()).isNotNull();

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();
            
            assertThat(data).containsKey("newAverage");
            assertThat(data).containsKey("totalRatings");
            assertThat(data).containsKey("userRating");
            assertThat(data.get("userRating")).isEqualTo(4.5);
            assertThat(data.get("totalRatings")).isEqualTo(1);
            assertThat(data.get("newAverage")).isEqualTo(4.5);
        }

        @Test
        @DisplayName("Should return updated statistics after multiple ratings")
        void shouldReturnUpdatedStatisticsAfterMultipleRatings() {
            // Given - Existing rating
            Rating existingRating = Rating.builder()
                    .average(3.0)
                    .count(2)
                    .distribution(new HashMap<>())
                    .build();
            testBook.setRating(existingRating);
            
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // When - Add third rating
            validRequest.setRating(5.0);
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();

            assertThat(data.get("totalRatings")).isEqualTo(3);
            assertThat(data.get("userRating")).isEqualTo(5.0);
            // newAverage should be calculated: (3.0*2 + 5.0)/3 = 11/3 ≈ 3.7
            assertThat(data.get("newAverage")).isEqualTo(3.7);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle database save failure gracefully")
        void shouldHandleDatabaseSaveFailureGracefully() {
            // Given
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

            // When
            ApiResponse response = ProductService.addRating("BOOK001", validRequest);

            // Then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).contains("Failed to add rating");
        }

        @Test
        @DisplayName("Should handle rating calculation error")
        void shouldHandleRatingCalculationError() {
            // Given - Corrupted rating data that might cause issues
            Rating corruptedRating = Rating.builder()
                    .average(null)
                    .count(null)
                    .distribution(null)
                    .build();
            testBook.setRating(corruptedRating);
            
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));

            // When & Then - Should handle gracefully or fix the corruption
            assertDoesNotThrow(() -> {
                ProductService.addRating("BOOK001", validRequest);
            });
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete rating workflow end-to-end")
        void shouldHandleCompleteRatingWorkflowEndToEnd() {
            // Given
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(testBook));
            when(ProductRepository.save(any(Product.class))).thenReturn(testBook);

            // When - Add multiple ratings sequentially
            // First rating
            validRequest.setRating(5.0);
            ApiResponse response1 = ProductService.addRating("BOOK001", validRequest);

            // Second rating  
            validRequest.setRating(3.0);
            ApiResponse response2 = ProductService.addRating("BOOK001", validRequest);

            // Third rating
            validRequest.setRating(4.0);
            ApiResponse response3 = ProductService.addRating("BOOK001", validRequest);

            // Then - All should succeed
            assertThat(response1.isSuccess()).isTrue();
            assertThat(response2.isSuccess()).isTrue();
            assertThat(response3.isSuccess()).isTrue();

            // Verify Product was saved 3 times
            verify(ProductRepository, times(3)).save(any(Product.class));

            // Verify final response contains correct data
            @SuppressWarnings("unchecked")
            Map<String, Object> finalData = (Map<String, Object>) response3.getData();
            assertThat(finalData.get("totalRatings")).isEqualTo(3);
            assertThat(finalData.get("userRating")).isEqualTo(4.0);
        }

        // @Test
        @DisplayName("Should maintain data consistency across operations - SKIPPED (Mockito capture issue)")
        void shouldMaintainDataConsistencyAcrossOperations_SKIPPED() {
            // Given - Create fresh Product instance for this test to avoid cross-test contamination
            Product freshProduct = Product.builder()
                    .id("BOOK001")
                    .title("Test Product")
                    .status(ProductStatus.ACTIVE)
                    .rating(null) // No rating initially
                    .build();
            
            when(ProductRepository.findById("BOOK001")).thenReturn(Optional.of(freshBook));

            
            // Capture all save operations to verify consistency
            ArgumentCaptor<Product> bookCaptor = ArgumentCaptor.forClass(Product.class);
            when(ProductRepository.save(bookCaptor.capture())).thenReturn(freshBook);            // When - Add multiple ratings
            validRequest.setRating(4.0);
            ProductService.addRating("BOOK001", validRequest);
            
            validRequest.setRating(5.0);
            ProductService.addRating("BOOK001", validRequest);

            // Then - Verify data consistency in each save
            var savedProducts = bookCaptor.getAllValues();
            
            // First save
            Product firstSave = savedProducts.get(0);
            assertThat(firstSave.getRating().getCount()).isEqualTo(1);
            assertThat(firstSave.getRating().getAverage()).isEqualTo(4.0);
            
            // Second save
            Product secondSave = savedProducts.get(1);
            assertThat(secondSave.getRating().getCount()).isEqualTo(2);
            assertThat(secondSave.getRating().getAverage()).isEqualTo(4.5);
        }
    }
}
