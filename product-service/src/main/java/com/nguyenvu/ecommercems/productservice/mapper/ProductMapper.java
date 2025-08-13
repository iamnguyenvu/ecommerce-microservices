package com.nguyenvu.ecommercems.productservice.mapper;

import com.nguyenvu.ecommercems.productservice.dto.ProductDTO;
import com.nguyenvu.ecommercems.productservice.model.Product;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Product entity and ProductDTO conversion
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProductMapper {

    /**
     * Convert Product entity to ProductDTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "publisher", source = "publisher")
    @Mapping(target = "publishedDate", source = "publishedDate")
    @Mapping(target = "publishedYear", source = "publishedYear")
    @Mapping(target = "edition", source = "edition")

    @Mapping(target = "physical", source = "physical")

    @Mapping(target = "Suppliers", source = "Suppliers")
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
    @Mapping(target = "seriesName", source = "seriesName")
    @Mapping(target = "seriesVolume", source = "seriesVolume")
    @Mapping(target = "seo", source = "seo")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "audit", source = "audit")

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProductDTO toDTO(Product Product);

    /**
     * Convert ProductDTO to Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductDTO dto);

    /**
     * Convert list of Product entities to list of BookDTOs
     */
    List<ProductDTO> toDTOList(List<Product> products);

    /**
     * Convert list of BookDTOs to list of Product entities
     */
    List<Product> toEntityList(List<ProductDTO> dtos);

    /**
    /**
     * Update existing Product entity with ProductDTO data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product Product);
}
