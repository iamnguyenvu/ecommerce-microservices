package com.nguyenvu.ecommercems.productservice.service.product.validation;

import com.nguyenvu.ecommercems.productservice.service.shared.exception.ProductValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class IsbnValidator {
    
    // Patterns for ISBN format validation
    private static final Pattern ISBN_13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern ISBN_10_PATTERN = Pattern.compile("^\\d{9}[\\dX]$");
    
    // Formatted patterns with hyphens/spaces
    private static final Pattern ISBN_13_FORMATTED = Pattern.compile("^\\d{3}-\\d{1}-\\d{3,7}-\\d{1,7}-\\d{1}$");
    private static final Pattern ISBN_10_FORMATTED = Pattern.compile("^\\d{1,7}-\\d{1,7}-\\d{1,7}-[\\dX]$");

    /**
     * Main validation method called by ProductValidator
     */
    public void validate(String isbn) {
        log.debug("Validating ISBN: {}", isbn);
        
        if (!StringUtils.hasText(isbn)) {
            throw new ProductValidationException("ISBN cannot be null or empty");
        }

        String cleanIsbn = cleanIsbn(isbn);
        
        if (cleanIsbn.length() == 13) {
            validateIsbn13(cleanIsbn);
        } else if (cleanIsbn.length() == 10) {
            validateIsbn10(cleanIsbn);
        } else {
            throw new ProductValidationException("Invalid ISBN format: " + isbn + ". ISBN must be 10 or 13 digits");
        }
    }

    /**
     * Clean ISBN by removing hyphens, spaces and converting to uppercase
     */
    private String cleanIsbn(String isbn) {
        return isbn.replaceAll("[\\s-]", "").toUpperCase();
    }

    /**
     * Check if clean ISBN matches basic format patterns
     */
    private boolean isValidIsbn13Format(String isbn) {
        return ISBN_13_PATTERN.matcher(isbn).matches();
    }

    private boolean isValidIsbn10Format(String isbn) {
        return ISBN_10_PATTERN.matcher(isbn).matches();
    }

    /**
     * Validate ISBN-13 with checksum
     */
    private void validateIsbn13(String isbn) {
        if (!isValidIsbn13Format(isbn)) {
            throw new ProductValidationException("Invalid ISBN-13 format: " + isbn);
        }
        
        // Calculate checksum for ISBN-13
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        int checkDigit = (10 - (sum % 10)) % 10;
        int actualCheckDigit = Character.getNumericValue(isbn.charAt(12));
        
        if (checkDigit != actualCheckDigit) {
            throw new ProductValidationException("Invalid ISBN-13 checksum: " + isbn + 
                ". Expected check digit: " + checkDigit + ", actual: " + actualCheckDigit);
        }
        
        log.debug("ISBN-13 validation successful: {}", isbn);
    }

    /**
     * Validate ISBN-10 with checksum
     */
    private void validateIsbn10(String isbn) {
        if (!isValidIsbn10Format(isbn)) {
            throw new ProductValidationException("Invalid ISBN-10 format: " + isbn);
        }
        
        // Calculate checksum for ISBN-10
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += digit * (10 - i);
        }
        
        int remainder = sum % 11;
        char expectedCheckChar = (remainder == 0) ? '0' : 
                                (remainder == 1) ? 'X' : 
                                (char) ('0' + (11 - remainder));
        
        char actualCheckChar = isbn.charAt(9);
        
        if (expectedCheckChar != actualCheckChar) {
            throw new ProductValidationException("Invalid ISBN-10 checksum: " + isbn + 
                ". Expected check character: " + expectedCheckChar + ", actual: " + actualCheckChar);
        }
        
        log.debug("ISBN-10 validation successful: {}", isbn);
    }

    /**
     * Format ISBN-13 with standard hyphen placement
     */
    public String formatIsbn13(String isbn) {
        String clean = cleanIsbn(isbn);
        if (clean.length() != 13) {
            throw new ProductValidationException("Cannot format non-13-digit ISBN: " + isbn);
        }
        
        // Standard ISBN-13 format: XXX-X-XXXXX-XXX-X
        return clean.substring(0, 3) + "-" +
               clean.substring(3, 4) + "-" +
               clean.substring(4, 9) + "-" +
               clean.substring(9, 12) + "-" +
               clean.substring(12);
    }

    /**
     * Format ISBN-10 with standard hyphen placement
     */
    public String formatIsbn10(String isbn) {
        String clean = cleanIsbn(isbn);
        if (clean.length() != 10) {
            throw new ProductValidationException("Cannot format non-10-digit ISBN: " + isbn);
        }
        
        // Common ISBN-10 format: X-XXX-XXXXX-X
        return clean.substring(0, 1) + "-" +
               clean.substring(1, 4) + "-" +
               clean.substring(4, 9) + "-" +
               clean.substring(9);
    }

    /**
     * Convert ISBN-10 to ISBN-13
     */
    public String convertIsbn10ToIsbn13(String isbn10) {
        String clean = cleanIsbn(isbn10);
        if (clean.length() != 10) {
            throw new ProductValidationException("Cannot convert non-10-digit ISBN: " + isbn10);
        }
        
        validateIsbn10(clean);
        
        // Add 978 prefix and remove ISBN-10 check digit
        String isbn13WithoutCheck = "978" + clean.substring(0, 9);
        
        // Calculate new checksum
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn13WithoutCheck.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        int checkDigit = (10 - (sum % 10)) % 10;
        String isbn13 = isbn13WithoutCheck + checkDigit;
        
        log.debug("Converted ISBN-10 {} to ISBN-13 {}", isbn10, isbn13);
        return isbn13;
    }

    /**
     * Check if ISBN is valid without throwing exception
     */
    public boolean isValid(String isbn) {
        try {
            validate(isbn);
            return true;
        } catch (ProductValidationException e) {
            log.debug("ISBN validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get ISBN type (10 or 13)
     */
    public int getIsbnType(String isbn) {
        if (!StringUtils.hasText(isbn)) {
            throw new ProductValidationException("ISBN cannot be null or empty");
        }
        
        String clean = cleanIsbn(isbn);
        return clean.length();
    }
}
