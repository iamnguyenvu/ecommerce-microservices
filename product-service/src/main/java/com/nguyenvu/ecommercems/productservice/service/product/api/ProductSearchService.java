package com.nguyenvu.ecommercems.productservice.service.Product.api;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.ProductSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductSearchService {
    List<ProductDTO> searchProducts(String searchText);

    Page<ProductDTO> searchProducts(String searchText, Pageable pageable);

    Page<ProductDTO> advancedSearch(ProductSearchCriteria criteria, Pageable pageable);

    List<ProductDTO> getProductsByAuthor(String authorId, int limit);

    List<ProductDTO> getProductsByAuthorName(String authorName, int limit);

    List<ProductDTO> getProductsByPublisher(String publisherId, int limit);

    List<ProductDTO> getProductsByPublisherName(String publisherName, int limit);

    List<ProductDTO> getProductsBySeries(String seriesId, int limit);

    List<ProductDTO> getProductsBySeriesName(String seriesName, int limit);

    List<ProductDTO> getProductsByCategory(String categoryId, int limit);

    List<ProductDTO> getProductsInMultipleCategories(List<String> categoryIds);

    List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int limit);

    List<ProductDTO> getDiscountedProducts();
}
