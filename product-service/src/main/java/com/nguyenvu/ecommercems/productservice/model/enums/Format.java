package com.nguyenvu.ecommercems.productservice.model.enums;

public enum Format {
    HARDCOVER("bìa cứng"),
    PAPERBACK("bìa mềm"),
    EBOOK("sách điện tử"),
    AUDIOBOOK("sách nói"),
    DIGITAL("tài liệu số");
    
    private final String vietnameseName;
    
    Format(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }
    
    public String getVietnameseName() {
        return vietnameseName;
    }
}
