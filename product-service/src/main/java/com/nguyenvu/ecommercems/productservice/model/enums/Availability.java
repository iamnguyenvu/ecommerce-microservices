package com.nguyenvu.ecommercems.productservice.model.enums;

public enum Availability {
    IN_STOCK("in-stock", "Còn hàng"),
    OUT_OF_STOCK("out-of-stock", "Hết hàng"),
    LOW_STOCK("low-stock", "Sắp hết hàng"),
    PRE_ORDER("pre-order", "Đặt trước"),
    BACK_ORDER("back-order", "Đặt hàng trước"),
    LIMITED("limited", "Số lượng có hạn");
    
    private final String code;
    private final String description;
    
    Availability(String code, String description) {
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
