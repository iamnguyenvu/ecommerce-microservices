package com.nguyenvu.ecommercems.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 10)
    private String phoneNumber;
    
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    // Future fields for phone verification
    @Builder.Default
    private Boolean phoneVerified = false;
    private String verificationCode;
    private Long verificationCodeExpiry;
}
