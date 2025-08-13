package com.nguyenvu.ecommercems.authservice.service;

import com.nguyenvu.ecommercems.authservice.dto.LoginRequest;
import com.nguyenvu.ecommercems.authservice.dto.RegisterRequest;
import com.nguyenvu.ecommercems.authservice.exception.InvalidCredentialsException;
import com.nguyenvu.ecommercems.authservice.exception.UserAlreadyExistsException;
import com.nguyenvu.ecommercems.authservice.exception.UserNotFoundException;
import com.nguyenvu.ecommercems.authservice.model.User;
import com.nguyenvu.ecommercems.authservice.model.UserRole;
import com.nguyenvu.ecommercems.authservice.repository.UserRepository;
import com.nguyenvu.ecommercems.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest registerRequest) {
        String normalizedPhoneNumber = normalizePhoneNumber(registerRequest.getPhoneNumber());
        
        if(userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
            throw new UserAlreadyExistsException("Phone number already exists");
        }

        User user = User.builder()
            .phoneNumber(normalizedPhoneNumber)
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .role(UserRole.USER)
            .phoneVerified(false) // TODO: Implement OTP verification
            .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public Map<String, String> login(LoginRequest loginRequest) {
        String normalizedPhoneNumber = normalizePhoneNumber(loginRequest.getPhoneNumber());
        
        User user = userRepository.findByPhoneNumber(normalizedPhoneNumber)
                .orElseThrow(() -> new UserNotFoundException("Phone number not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getPhoneNumber());
        return Map.of("token", token, "phoneNumber", user.getPhoneNumber());
    }
    
    /**
     * Normalize Vietnamese phone number format
     * Removes spaces, dashes and ensures proper format
     */
    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        
        // Remove all non-digit characters
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        
        // Handle +84 prefix
        if (cleaned.startsWith("84") && cleaned.length() == 11) {
            cleaned = "0" + cleaned.substring(2);
        }
        
        return cleaned;
    }
}
