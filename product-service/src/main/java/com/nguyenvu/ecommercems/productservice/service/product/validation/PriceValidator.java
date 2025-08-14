package com.nguyenvu.ecommercems.productservice.service.product.validation;

import com.nguyenvu.ecommercems.productservice.model.embedded.Pricing;
import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Slf4j
@RequiredArgsConstructor
public class PriceValidator {
    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
    private static final BigDecimal MAX_PRICE = new BigDecimal("10000000.00");
    private static final BigDecimal MAX_DISCOUNT_PERCENT = new BigDecimal("99");

    public void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }

        if (price.compareTo(MIN_PRICE) < 0 || price.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException("Price must be between " + MIN_PRICE + " and " + MAX_PRICE);
        }

        if (price.scale() > 2) {
            throw new IllegalArgumentException("Price can have a maximum of two decimal places");
        }
    }

    public void validatePricing(Pricing pricing) {
        if (pricing == null) {
            throw new ProductValidationException("Pricing information is required");
        }
        
        // Validate list price
        if (pricing.getListPrice() == null) {
            throw new ProductValidationException("List price is required");
        }
        validatePrice(pricing.getListPrice());
        
        // Validate sale price if exists
        if (pricing.getSalePrice() != null) {
            validatePrice(pricing.getSalePrice());
            validatePriceRelationship(pricing.getListPrice(), pricing.getSalePrice());
        }
        
        // Note: Cost price is not in the current Pricing model
        
        // Validate discount percentage if exists
        if (pricing.getDiscountPercent() != null) {
            validateDiscountPercentage(pricing.getDiscountPercent());
            
            // Cross-validate with calculated discount
            if (pricing.getSalePrice() != null) {
                BigDecimal calculatedDiscount = calculateDiscountedPrice(
                    pricing.getListPrice(), pricing.getSalePrice());
                BigDecimal providedDiscount = new BigDecimal(pricing.getDiscountPercent());
                
                // Allow small difference due to rounding
                if (calculatedDiscount.subtract(providedDiscount).abs().compareTo(new BigDecimal("0.1")) > 0) {
                    log.warn("Discount percentage mismatch: calculated {}%, provided {}%", 
                        calculatedDiscount, providedDiscount);
                }
            }
        }
        
        // Validate currency
        if (pricing.getCurrency() != null) {
            BigDecimal effectivePrice = pricing.getSalePrice() != null ? 
                pricing.getSalePrice() : pricing.getListPrice();
            validatePriceForCurrency(effectivePrice, pricing.getCurrency());
        }
    }

    private void validatePriceRelationship(BigDecimal originalPrice, BigDecimal salePrice) {
        if (salePrice.compareTo(originalPrice) > 0) {
            throw new ProductValidationException(
                    "Sale price (" + salePrice + ") cannot be greater than original price (" + originalPrice + ")"
            );
        }

        BigDecimal discount = originalPrice.subtract(salePrice);
        BigDecimal discountPercent = discount.divide(originalPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        if (discountPercent.compareTo(MAX_DISCOUNT_PERCENT) > 0) {
            throw new ProductValidationException(
                    "Discount percentage (" + discountPercent + "%) cannot exceed " + MAX_DISCOUNT_PERCENT + "%"
            );
        }
    }

    private void validateDiscountPercentage(Integer discountPercentage) {
        if (discountPercentage < 0) {
            throw new ProductValidationException("Discount percentage cannot be negative");
        }

        if (BigDecimal.valueOf(discountPercentage).compareTo(MAX_DISCOUNT_PERCENT) > 0) {
            throw new ProductValidationException("Discount percentage cannot exceed " + MAX_DISCOUNT_PERCENT + "%");
        }
    }

    private void validateDiscountPercentage(BigDecimal discountPercentage) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductValidationException("Discount percentage cannot be negative");
        }

        if (discountPercentage.compareTo(MAX_DISCOUNT_PERCENT) > 0) {
            throw new ProductValidationException("Discount percentage cannot exceed " + MAX_DISCOUNT_PERCENT + "%");
        }
    }

    private void validateCostPrice(BigDecimal costPrice, BigDecimal salePrice) {
        if (costPrice.compareTo(salePrice) > 0) {
            log.warn("Cost price ({}) is higher than sale price ({}). This may result in loss.",
                    costPrice, salePrice);
        }

        BigDecimal profit = salePrice.subtract(costPrice);
        BigDecimal profitMargin = profit.divide(costPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (profitMargin.compareTo(new BigDecimal("-50")) < 0) {
            log.warn("Profit margin is very negative ({}%). Cost: {}, Sale: {}",
                    profitMargin.setScale(2, RoundingMode.HALF_UP), costPrice, salePrice);
        }
    }

    public void validatePriceChange(BigDecimal oldPrice, BigDecimal newPrice) {
        validatePrice(newPrice);

        if (oldPrice != null) {
            BigDecimal changePercent = newPrice.subtract(oldPrice)
                    .divide(oldPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .abs();

            if (changePercent.compareTo(new BigDecimal("50")) > 0) {
                log.warn("Large price change detected: {} -> {} ({}%)",
                        oldPrice, newPrice, changePercent.setScale(2, RoundingMode.HALF_UP));
            }
        }
    }

    public void validateBulkPricing(BigDecimal basePrice, int quantity) {
        validatePrice(basePrice);

        if (quantity <= 0) {
            throw new ProductValidationException("Quantity must be greater than zero");
        }

        if (quantity > 5000) {
            throw new ProductValidationException("Quantity cannot exceed 1000");
        }

        BigDecimal totalValue = basePrice.multiply(BigDecimal.valueOf(quantity));

        if (totalValue.compareTo(MAX_PRICE) > 0) {
            log.warn("Large bulk order value: {} (qty: {} x price: {})", totalValue, quantity, basePrice);
        }
    }

    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, BigDecimal salePrice) {
        if (originalPrice == null || salePrice == null) {
            return BigDecimal.ZERO;
        }

        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = originalPrice.subtract(salePrice);
        return discount.divide(originalPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSalePrice(BigDecimal originalPrice, BigDecimal discountPercentage) {
        validatePrice(originalPrice);
        validateDiscountPercentage(discountPercentage);

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

        return originalPrice.multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void validatePriceForCurrency(BigDecimal price, String currencyCode) {
        validatePrice(price);

        // Currency-specific validation rules
        switch (currencyCode.toUpperCase()) {
            case "USD":
            case "EUR":
                // Standard validation already applied
                break;
            case "JPY":
                // JPY typically doesn't use decimal places
                if (price.scale() > 0) {
                    log.warn("JPY price has decimal places: {}", price);
                }
                break;
            case "VND":
                // Vietnamese Dong - typically larger numbers
                if (price.compareTo(new BigDecimal("1000")) < 0) {
                    log.warn("VND price seems unusually low: {}", price);
                }
                break;
            default:
                log.debug("No specific validation rules for currency: {}", currencyCode);
        }
    }

}
