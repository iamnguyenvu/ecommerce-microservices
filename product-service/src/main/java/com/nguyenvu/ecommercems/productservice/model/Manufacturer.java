package com.nguyenvu.ecommercems.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Manufacturer entity for standalone manufacturer management
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "manufacturers")
public class Manufacturer {
    
    @Id
    private String id;
    
    private String name;
    private String description;
    private String website;
    private String country;
    private String contactInfo;
}
