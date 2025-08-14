package com.nguyenvu.ecommercems.productservice.mapper;

import com.nguyenvu.ecommercems.productservice.dto.CreateProductRequest;
import com.nguyenvu.ecommercems.productservice.model.embedded.Supplier;
import org.mapstruct.*;

/**
 * MapStruct mapper for Supplier entity and DTO conversions
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SupplierMapper {

    /**
     * Convert SupplierRequest to Supplier embedded document
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "website", source = "website")
    @Mapping(target = "country", constant = "Vietnam") // Default country if not specified
    Supplier fromRequest(CreateProductRequest.SupplierRequest request);

    /**
     * Convert Supplier embedded document to SupplierRequest
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "website", source = "website")
    CreateProductRequest.SupplierRequest toRequest(Supplier supplier);
}
