package com.nguyenvu.ecommercems.productservice.model.enums;

public enum Language {
    VIETNAMESE("vi", "Tiếng Việt"),
    ENGLISH("en", "English"),
    CHINESE("zh", "中文"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    SPANISH("es", "Español"),
    RUSSIAN("ru", "Русский"),
    THAI("th", "ไทย"),
    OTHER("other", "Khác");
    
    private final String code;
    private final String displayName;
    
    Language(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
