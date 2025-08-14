package com.nguyenvu.ecommercems.productservice.service.product.api;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;

import java.util.List;

public interface ProductCrudService {
    // ===== BASIC CRUD ======
    ProductDTO createProduct(ProductDTO ProductDTO);
    ProductDTO updateProduct(ProductDTO ProductDTO);
    ProductDTO patchPrice(String bookId, Double price);
    void deleteProduct(String bookId);

    // ===== BATCH OPERATIONS ======
    List<ProductDTO> createProducts(List<ProductDTO> bookDTOs);
    void deleteProductsByIds(List<String> bookIds);
    void updateStock(String bookId, Integer quantity);
    void adjustStock(String bookId, Integer delta, String reason);
}
