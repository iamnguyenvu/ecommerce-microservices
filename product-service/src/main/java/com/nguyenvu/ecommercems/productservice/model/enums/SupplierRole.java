package com.nguyenvu.ecommercems.productservice.model.enums;

public enum SupplierRole {
    PRIMARY_SUPPLIER("nhà cung cấp chính"),
    SECONDARY_SUPPLIER("nhà cung cấp phụ"),
    DISTRIBUTOR("nhà phân phối"),
    MANUFACTURER("nhà sản xuất"),
    WHOLESALER("nhà bán buôn"),
    RETAILER("nhà bán lẻ"),
    OTHER("khác");
    
    private final String vietnameseName;
    
    SupplierRole(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }
    
    public String getVietnameseName() {
        return vietnameseName;
    }
}
