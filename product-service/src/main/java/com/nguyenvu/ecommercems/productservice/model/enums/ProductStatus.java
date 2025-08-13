package com.nguyenvu.ecommercems.productservice.model.enums;

public enum ProductStatus {
    ACTIVE("active", "Đang hoạt động"),
    INACTIVE("inactive", "Ngừng hoạt động"),
    DISCONTINUED("discontinued", "Ngừng sản xuất"),
    PRE_ORDER("pre-order", "Đặt trước");
    
    private final String code;
    private final String description;
    
    ProductStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}
