package com.nguyenvu.ecommercems.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatsDTO {
    private Double averageRating;
    private Integer totalRatings;
    private Integer fiveStar;
    private Integer fourStar;
    private Integer threeStar;
    private Integer twoStar;
    private Integer oneStar;
    private Double percentageFiveStar;
    private Double percentageFourStar;
    private Double percentageThreeStar;
    private Double percentageTwoStar;
    private Double percentageOneStar;
}
