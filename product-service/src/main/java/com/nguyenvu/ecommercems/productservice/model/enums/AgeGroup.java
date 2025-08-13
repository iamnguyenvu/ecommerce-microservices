package com.nguyenvu.ecommercems.productservice.model.enums;

public enum AgeGroup {
    CHILDREN("thiếu nhi", "Thiếu nhi"),
    TEEN("thanh thiếu niên", "Thanh thiếu niên"),
    ADULT("người lớn", "Người lớn"),
    ALL_AGES("mọi lứa tuổi", "Mọi lứa tuổi");
    
    private final String code;
    private final String description;
    
    AgeGroup(String code, String description) {
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
