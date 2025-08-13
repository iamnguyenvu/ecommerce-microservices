package com.nguyenvu.ecommercems.authservice.service;

import com.nguyenvu.ecommercems.authservice.dto.RegisterRequest;
import com.nguyenvu.ecommercems.authservice.dto.LoginRequest;
import com.nguyenvu.ecommercems.authservice.exception.InvalidCredentialsException;
import com.nguyenvu.ecommercems.authservice.exception.UserAlreadyExistsException;
import com.nguyenvu.ecommercems.authservice.exception.UserNotFoundException;
import com.nguyenvu.ecommercems.authservice.model.User;
import com.nguyenvu.ecommercems.authservice.model.UserRole;
import com.nguyenvu.ecommercems.authservice.repository.UserRepository;
import com.nguyenvu.ecommercems.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;


    @BeforeEach
    void setUp() {
        // Create RegisterRequest with Vietnamese phone number
        registerRequest = RegisterRequest.builder()
                .phoneNumber("0987654321")
                .password("Password123!")
                .build();

        // Create LoginRequest with Vietnamese phone number  
        loginRequest = LoginRequest.builder()
                .phoneNumber("0987654321")
                .password("Password123!")
                .build();

        // Create test User with phone number
        testUser = User.builder()
                .phoneNumber("0987654321")
                .password("encodedPassword")
                .role(UserRole.USER)
                .phoneVerified(false)
                .build();

    }
    
    // ==========================================
    // REGISTER TESTS
    // ==========================================
    
    @Test
    void register_ShouldReturnSuccessMessage_WhenValidRequest() {
        // ARRANGE
        when(userRepository.existsByPhoneNumber("0987654321")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");

        // ACT
        String result = authService.register(registerRequest);

        // ASSERT
        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).existsByPhoneNumber("0987654321");
        verify(passwordEncoder, times(1)).encode("Password123!");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void register_ShouldThrowException_WhenPhoneNumberAlreadyExists() {
        // ARRANGE
        when(userRepository.existsByPhoneNumber("0987654321")).thenReturn(true);

        // ACT & ASSERT
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Phone number already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByPhoneNumber("0987654321");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldNormalizePhoneNumber_WhenPhoneNumberHas84Prefix() {
        // ARRANGE
        RegisterRequest requestWith84 = RegisterRequest.builder()
                .phoneNumber("84987654321")
                .password("Password123!")
                .build();
        
        when(userRepository.existsByPhoneNumber("0987654321")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");

        // ACT
        String result = authService.register(requestWith84);

        // ASSERT
        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).existsByPhoneNumber("0987654321");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    // ==========================================
    // LOGIN TESTS
    // ==========================================
    
    @Test
    void login_ShouldReturnTokenAndPhoneNumber_WhenValidCredentials() {
        // ARRANGE
        when(userRepository.findByPhoneNumber("0987654321")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("0987654321")).thenReturn("jwt.token.here");
        
        // ACT
        Map<String, String> result = authService.login(loginRequest);
        
        // ASSERT
        assertEquals("jwt.token.here", result.get("token"));
        assertEquals("0987654321", result.get("phoneNumber"));
        verify(userRepository, times(1)).findByPhoneNumber("0987654321");
        verify(passwordEncoder, times(1)).matches("Password123!", "encodedPassword");
        verify(jwtUtil, times(1)).generateToken("0987654321");
    }
    
    @Test
    void login_ShouldThrowException_WhenPhoneNumberNotFound() {
        // ARRANGE
        when(userRepository.findByPhoneNumber("0987654321")).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Phone number not found", exception.getMessage());
        verify(userRepository, times(1)).findByPhoneNumber("0987654321");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any());
    }
    
    @Test
    void login_ShouldThrowException_WhenInvalidPassword() {
        // ARRANGE
        when(userRepository.findByPhoneNumber("0987654321")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(false);
        
        // ACT & ASSERT
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, times(1)).findByPhoneNumber("0987654321");
        verify(passwordEncoder, times(1)).matches("Password123!", "encodedPassword");
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void login_ShouldNormalizePhoneNumber_WhenLoginWith84Prefix() {
        // ARRANGE
        LoginRequest requestWith84 = LoginRequest.builder()
                .phoneNumber("84987654321")
                .password("Password123!")
                .build();
                
        when(userRepository.findByPhoneNumber("0987654321")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("0987654321")).thenReturn("jwt.token.here");
        
        // ACT
        Map<String, String> result = authService.login(requestWith84);
        
        // ASSERT
        assertEquals("jwt.token.here", result.get("token"));
        assertEquals("0987654321", result.get("phoneNumber"));
        verify(userRepository, times(1)).findByPhoneNumber("0987654321");
    }
}