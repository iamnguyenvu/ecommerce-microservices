package com.nguyenvu.ecommercems.productservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for product list operations with pagination
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductListResponse {
    
    // ===== RESPONSE DATA =====
    private List<ProductDTO> products;
    private Boolean success;
    private String message;
    
    // ===== PAGINATION INFO =====
    private PaginationInfo pagination;
    
    // ===== SEARCH METADATA =====
    private SearchMetadata search;
    
    // ===== NESTED CLASSES =====
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaginationInfo {
        private Integer currentPage;
        private Integer pageSize;
        private Integer totalPages;
        private Long totalElements;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private Integer nextPage;
        private Integer previousPage;
        private Long offset;
        private Integer numberOfElements;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchMetadata {
        private String query;
        private String sortBy;
        private String sortDirection;
        private List<String> appliedFilters;
        private Long searchTime;
        private String searchId;
        private Integer resultsFound;
        private Boolean hasFilters;
        private String suggestion;
    }
    
    // ===== FACTORY METHODS =====
    
    /**
     * Create a success response with products and pagination
     */
    public static ProductListResponse success(List<ProductDTO> products, PaginationInfo pagination) {
        return ProductListResponse.builder()
                .products(products)
                .pagination(pagination)
                .success(true)
                .message("Products retrieved successfully")
                .build();
    }
    
    /**
     * Create a success response with search metadata
     */
    public static ProductListResponse success(List<ProductDTO> products, PaginationInfo pagination, 
                                            SearchMetadata searchMetadata) {
        return ProductListResponse.builder()
                .products(products)
                .pagination(pagination)
                .search(searchMetadata)
                .success(true)
                .message("Products retrieved successfully")
                .build();
    }
    
    /**
     * Create an empty success response (no products found)
     */
    public static ProductListResponse empty(String message) {
        return ProductListResponse.builder()
                .products(List.of())
                .success(true)
                .message(message != null ? message : "No products found")
                .pagination(PaginationInfo.builder()
                        .currentPage(0)
                        .pageSize(0)
                        .totalPages(0)
                        .totalElements(0L)
                        .hasNext(false)
                        .hasPrevious(false)
                        .numberOfElements(0)
                        .build())
                .build();
    }
    
    /**
     * Create an error response
     */
    public static ProductListResponse error(String message) {
        return ProductListResponse.builder()
                .products(List.of())
                .success(false)
                .message(message)
                .build();
    }
    
    /**
     * Create pagination info
     */
    public static PaginationInfo createPagination(int currentPage, int pageSize, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        return PaginationInfo.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .hasNext(currentPage < totalPages - 1)
                .hasPrevious(currentPage > 0)
                .nextPage(currentPage < totalPages - 1 ? currentPage + 1 : null)
                .previousPage(currentPage > 0 ? currentPage - 1 : null)
                .offset((long) currentPage * pageSize)
                .numberOfElements((int) Math.min(pageSize, totalElements - ((long) currentPage * pageSize)))
                .build();
    }
    
    /**
     * Create search metadata
     */
    public static SearchMetadata createSearchMetadata(String query, String sortBy, String sortDirection,
                                                    List<String> appliedFilters, long searchTime) {
        return SearchMetadata.builder()
                .query(query)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .appliedFilters(appliedFilters)
                .searchTime(searchTime)
                .hasFilters(appliedFilters != null && !appliedFilters.isEmpty())
                .build();
    }
}
