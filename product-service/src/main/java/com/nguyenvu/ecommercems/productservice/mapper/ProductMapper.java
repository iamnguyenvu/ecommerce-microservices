package com.nguyenvu.ecommercems.productservice.mapper;

import com.nguyenvu.ecommercems.productservice.dto.CreateProductRequest;
import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.dto.UpdateProductRequest;
import com.nguyenvu.ecommercems.productservice.model.Product;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Product entity and DTO conversions
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    uses = {ManufacturerMapper.class, SupplierMapper.class}
)
public interface ProductMapper {

    // ===== ENTITY TO DTO MAPPINGS =====

    /**
     * Convert Product entity to ProductDTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "subtitle", source = "subtitle")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "manufacturer", source = "manufacturer")
    @Mapping(target = "manufacturedYear", source = "manufacturedYear")
    @Mapping(target = "releaseDate", source = "releaseDate")
    @Mapping(target = "launchTime", source = "launchTime")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "physical", source = "physical")
    @Mapping(target = "suppliers", source = "suppliers")
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "subjects", source = "subjects")
    @Mapping(target = "ageGroup", source = "ageGroup")
    @Mapping(target = "pricing", source = "pricing")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "reservedQuantity", source = "reservedQuantity")
    @Mapping(target = "availableQuantity", source = "availableQuantity")
    @Mapping(target = "featured", source = "featured")
    @Mapping(target = "sales", source = "sales")
    @Mapping(target = "seriesId", source = "seriesId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProductDTO toDTO(Product product);

    // ===== DTO TO ENTITY MAPPINGS =====

    /**
     * Convert ProductDTO to Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductDTO dto);

    /**
     * Convert CreateProductRequest to Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stockQuantity", source = "initialStockQuantity")
    @Mapping(target = "availability", expression = "java(determineAvailability(request))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "reservedQuantity", constant = "0")
    @Mapping(target = "availableQuantity", source = "initialStockQuantity")
    Product fromCreateRequest(CreateProductRequest request);

    /**
     * Update existing Product entity with UpdateProductRequest data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromUpdateRequest(UpdateProductRequest request, @MappingTarget Product product);

    /**
     * Update existing Product entity with ProductDTO data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product product);

    // ===== LIST MAPPINGS =====

    /**
     * Convert list of Product entities to list of ProductDTOs
     */
    List<ProductDTO> toDTOList(List<Product> products);

    /**
     * Convert list of ProductDTOs to list of Product entities
     */
    List<Product> toEntityList(List<ProductDTO> dtos);

    // ===== HELPER METHODS =====

    /**
     * Determine availability based on stock and product type
     */
    default com.nguyenvu.ecommercems.productservice.model.enums.Availability determineAvailability(CreateProductRequest request) {
        if (request.getInitialStockQuantity() == null || request.getInitialStockQuantity() <= 0) {
            return com.nguyenvu.ecommercems.productservice.model.enums.Availability.OUT_OF_STOCK;
        }
        
        // Digital products are always available
        if (request.getType() != null && 
            request.getType() == com.nguyenvu.ecommercems.productservice.model.enums.ProductType.DIGITAL) {
            return com.nguyenvu.ecommercems.productservice.model.enums.Availability.IN_STOCK;
        }
        
        return com.nguyenvu.ecommercems.productservice.model.enums.Availability.IN_STOCK;
    }
}
