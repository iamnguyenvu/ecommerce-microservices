package com.nguyenvu.ecommercems.productservice.repository;

import com.nguyenvu.ecommercems.productservice.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    // ===== BASIC QUERIES =====
    @Query("{'bookId': ?0, 'userId': ?1, 'status': 'APPROVED'}")
    Optional<Review> findByBookIdAndUserId(String bookId, String userId);

    @Query("{'bookId': ?0, 'status': 'APPROVED'}")
    Optional<Review> findByBookId(String bookId);

    @Query("{'userId': ?0, 'status': 'APPROVED'}")
    Optional<Review> findByUserId(String userId);

    // ===== VALIDATION QUERIES =====
    @Query(value = "{'bookId': ?0, 'status': 'APPROVED'}", count = true)
    long countByBookId(String bookId);

    @Query(value = "{'userId': ?0, 'status': 'APPROVED'}", count = true)
    long countByUserId(String userId);

    @Query(value = "{'bookId': ?0, 'status': 'APPROVED'}", exists = true)
    Boolean existsByBookId(String bookId);

    // ===== SEARCH QUERIES =====
    @Query("{'bookId': ?0, 'status': 'APPROVED', 'rating': {$gte: ?1}}")
    List<Review> findApprovedReviewsByBookIdAndRating(String bookId, Double rating);

    @Query("{'bookId': ?0, 'status': 'APPROVED', 'rating': {$gte: ?1}, 'verifiedPurchase': true}")
    List<Review> findVerifiedApprovedReviewsByBookIdAndRating(String bookId, Double rating);

    @Query("{'bookId': ?0, 'status': 'APPROVED', 'rating': {$gte: ?1}, 'verifiedPurchase': true, 'helpfulCount': {$gte: ?2}}")
    List<Review> findVerifiedApprovedReviewsByBookIdAndRatingWithHelpfulCount(String bookId, Double rating, Integer helpfulCount);

    @Query("{'rating': {$gte: ?0, $lte: ?1}, 'status': 'APPROVED'}")
    List<Review> findByRatingRange(Double minRate, Double maxRate);
}
