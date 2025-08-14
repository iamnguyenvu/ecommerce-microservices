package com.nguyenvu.ecommercems.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Supplier entity for standalone supplier management
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "suppliers")
public class Supplier {
    
    @Id
    private String id;
    
    private String name;
    private String description;
    private String website;
    private String country;
    private String contactInfo;
    private String supplierType;
    private String paymentTerms;
    private Integer deliveryTime; // in days
    private Integer minimumOrder;
}
