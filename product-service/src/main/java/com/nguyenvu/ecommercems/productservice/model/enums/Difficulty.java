package com.nguyenvu.ecommercems.productservice.model.enums;

public enum Difficulty {
    BEGINNER("cơ bản"),
    INTERMEDIATE("trung cấp"),
    ADVANCED("nâng cao"),
    EXPERT("chuyên gia"),
    ALL_LEVELS("mọi cấp độ");
    
    private final String vietnameseName;
    
    Difficulty(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }
    
    public String getVietnameseName() {
        return vietnameseName;
    }
}
