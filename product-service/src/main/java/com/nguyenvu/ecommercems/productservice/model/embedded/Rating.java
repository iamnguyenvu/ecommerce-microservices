package com.nguyenvu.ecommercems.productservice.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced Rating embedded object
 * Backward compatible while adding new features
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

    private Double average;
    private Integer count;
    private Map<String, Integer> distribution;
    private Map<String, Double> percentages;

    private Integer verifiedCount;              // Number of verified purchase ratings

    private LocalDateTime firstRatingDate;
    private LocalDateTime lastRatingDate;
    
    // ===== HELPER METHODS =====
    
    /**
     * Initialize distribution map if null (backward compatibility)
     */
    public Map<String, Integer> getDistribution() {
        if (distribution == null) {
            distribution = new HashMap<>();
            distribution.put("5", 0);
            distribution.put("4", 0);
            distribution.put("3", 0);
            distribution.put("2", 0);
            distribution.put("1", 0);
        }
        return distribution;
    }
    
    /**
     * Initialize percentages map if null (backward compatibility)
     */
    public Map<String, Double> getPercentages() {
        if (percentages == null) {
            percentages = new HashMap<>();
            percentages.put("5", 0.0);
            percentages.put("4", 0.0);
            percentages.put("3", 0.0);
            percentages.put("2", 0.0);
            percentages.put("1", 0.0);
        }
        return percentages;
    }
    
    /**
     * Calculate percentages based on distribution
     */
    public void calculatePercentages() {
        if (count == null || count == 0) {
            getPercentages().replaceAll((k, v) -> 0.0);
            return;
        }
        
        Map<String, Integer> dist = getDistribution();
        Map<String, Double> pct = getPercentages();
        
        for (String star : new String[]{"5", "4", "3", "2", "1"}) {
            int starCount = dist.getOrDefault(star, 0);
            double percentage = Math.round((starCount * 100.0 / count) * 10.0) / 10.0;
            pct.put(star, percentage);
        }
    }

    public void addRating(double ratingValue) {
        // Validate rating (1-5)
        if(ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Update count
        count = (count == null) ? 1 : count + 1;

        // Calculate new average
        average = (average == null) ? ratingValue : (average * (count - 1) + ratingValue) / count;

        // Initialize and update distribution
        Map<String, Integer> dist = getDistribution();
        String starKey = String.valueOf((int) Math.round(ratingValue));
        dist.put(starKey, dist.getOrDefault(starKey, 0) + 1);

        // Calculate percentages
        calculatePercentages();
    }

    private void calculateDistribution() {
        if (distribution == null || count == 0) {
            return;
        }

        if (percentages == null) {
            percentages = new HashMap<>();
        }

        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / count;
            percentages.put(entry.getKey(), Math.round(percentage * 10.0) / 10.0);
        }
    }

    // ===== BUISNESS LOGIC =====

    @Transient
    public Boolean isPopular() {
        return count != null && count >= 10;
    }

    @Transient
    public Boolean isRecent() {
        return lastRatingDate != null
                && lastRatingDate.isAfter(LocalDateTime.now().minusDays(6));
    }

    @Transient
    public Double getRoundedAverage() {
        if (average == null) {
            return 0.0;
        }
        return Math.round(average * 10.0) / 10.0; // Round to 1 decimal place
    }

    // ===== BACKWARD COMPATIBILITY =====
    
    @Transient
    public Integer getTotalRatings() {
        return count;
    }
    
    @Transient
    public Integer getFiveStars() {
        return getDistribution().get("5");
    }
    
    @Transient
    public Integer getFourStars() {
        return getDistribution().get("4");
    }
    
    @Transient
    public Integer getThreeStars() {
        return getDistribution().get("3");
    }
    
    @Transient
    public Integer getTwoStars() {
        return getDistribution().get("2");
    }
    
    @Transient
    public Integer getOneStar() {
        return getDistribution().get("1");
    }
}
