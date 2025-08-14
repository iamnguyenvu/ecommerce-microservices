package com.nguyenvu.ecommercems.productservice.mapper;

import com.nguyenvu.ecommercems.productservice.dto.CreateProductRequest;
import com.nguyenvu.ecommercems.productservice.model.embedded.Manufacturer;
import org.mapstruct.*;

/**
 * MapStruct mapper for Manufacturer entity and DTO conversions
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ManufacturerMapper {

    /**
     * Convert ManufacturerRequest to Manufacturer embedded document
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "website", source = "website")
    @Mapping(target = "contactEmail", source = "email")
    @Mapping(target = "manufacturerId", ignore = true) // Will be set separately
    Manufacturer fromRequest(CreateProductRequest.ManufacturerRequest request);

    /**
     * Convert Manufacturer embedded document to ManufacturerRequest
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "website", source = "website")
    @Mapping(target = "email", source = "contactEmail")
    @Mapping(target = "description", ignore = true) // Not stored in embedded model
    CreateProductRequest.ManufacturerRequest toRequest(Manufacturer manufacturer);
}
