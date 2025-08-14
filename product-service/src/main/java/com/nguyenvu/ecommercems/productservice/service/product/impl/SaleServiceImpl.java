package com.nguyenvu.ecommercems.productservice.service.product.impl;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.ProductSalesStatsDTO;
import com.nguyenvu.ecommercems.productservice.mapper.ProductMapper;
import com.nguyenvu.ecommercems.productservice.model.Product;
import com.nguyenvu.ecommercems.productservice.service.product.base.AbstractProductService;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SaleServiceImpl extends AbstractProductService {
    private final ProductMapper ProductMapper;

    public void recordSale(String bookId, Integer quantity) {
    }

    public void recordSale(String bookId, Integer quantity, String orderId, BigDecimal unitPrice) {

    }

    public void recordSales(Map<String,Integer> saleDate) {

    }

    public List<ProductDTO> getBestsellers(int limit) {
        log.info("getBestsellers");

        try {
            if (limit <= 0) {
                throw new ProductServiceException("Limit must be greater than zero");
            }

            List<Product> products = ProductRepository.findBestsellers();
            return products.stream()
                    .map(ProductMapper::toDTO)
                    .toList();

        } catch (Exception e) {
            log.warn("Failed to get bestsellers", e);
            throw new ProductServiceException("Failed to get bestsellers: " + e.getMessage());
        }
    }

    public List<ProductDTO> getDailyBestsellers(int limit) {
        log.info("getDailyBestsellers");
        try {
            if (limit <= 0) {
                throw new ProductServiceException("Limit must be greater than zero");
            }

            List<Product> products = ProductRepository.findDailyBestsellers();
            return products.stream()
                    .map(ProductMapper::toDTO)
                    .toList();

        } catch (Exception e) {
            log.warn("Failed to get daily bestsellers", e);
            throw new ProductServiceException("Failed to get daily bestsellers: " + e.getMessage());
        }
    }

    public List<ProductDTO> getWeeklyBestsellers(int limit) {
        log.info("getWeeklyBestsellers");

        try {
            if (limit <= 0) {
                throw new ProductServiceException("Limit must be greater than zero");
            }

            List<Product> products = ProductRepository.findWeeklyBestsellers();
            return products.stream()
                    .map(ProductMapper::toDTO)
                    .toList();

        } catch (Exception e) {
            log.warn("Failed to get weekly bestsellers", e);
            throw new ProductServiceException("Failed to get weekly bestsellers: " + e.getMessage());
        }
    }

    public List<ProductDTO> getMonthlyBestsellers(int limit) {
        log.info("getMonthlyBestsellers");

        try {
            if (limit <= 0) {
                throw new ProductServiceException("Limit must be greater than zero");
            }

            List<Product> products = ProductRepository.findMonthlyBestsellers();
            return products.stream()
                    .map(ProductMapper::toDTO)
                    .toList();

        } catch (Exception e) {
            log.warn("Failed to get monthly bestsellers", e);
            throw new ProductServiceException("Failed to get monthly bestsellers: " + e.getMessage());
        }
    }

    public List<ProductDTO> getYearlyBestsellers(int limit) {
        log.info("getYearlyBestsellers");

        try {
            if (limit <= 0) {
                throw new ProductServiceException("Limit must be greater than zero");
            }

            List<Product> products = ProductRepository.findYearlyBestsellers();
            return products.stream()
                    .map(ProductMapper::toDTO)
                    .toList();

        } catch (Exception e) {
            log.warn("Failed to get yearly bestsellers", e);
            throw new ProductServiceException("Failed to get yearly bestsellers: " + e.getMessage());
        }
    }

    public ProductSalesStatsDTO getSalesStats(String bookId) {
        log.info("getSalesStats for bookId: {}", bookId);

        try {
            Product Product = ProductRepository.findById(bookId)
                    .orElseThrow(() -> new ProductServiceException("Product not found with ID: " + bookId));

            return ProductSalesStatsDTO.builder()
                    //
                    .build();

        } catch (Exception e) {
            log.warn("Failed to get sales stats for Product ID: {}", bookId, e);
            throw new ProductServiceException("Failed to get sales stats for Product ID: " + bookId, e);
        }
    }

}
