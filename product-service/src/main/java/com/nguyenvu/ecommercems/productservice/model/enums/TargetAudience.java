package com.nguyenvu.ecommercems.productservice.model.enums;

public enum TargetAudience {
    CHILDREN("trẻ em"),
    TEENAGERS("thanh thiếu niên"),
    YOUNG_ADULTS("người trẻ"),
    ADULTS("người lớn"),
    SENIORS("người cao tuổi"),
    GENERAL("tổng quát");
    
    private final String vietnameseName;
    
    TargetAudience(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }
    
    public String getVietnameseName() {
        return vietnameseName;
    }
}
