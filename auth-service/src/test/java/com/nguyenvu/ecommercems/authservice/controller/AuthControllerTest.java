package com.nguyenvu.ecommercems.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenvu.ecommercems.authservice.dto.LoginRequest;
import com.nguyenvu.ecommercems.authservice.dto.RegisterRequest;
import com.nguyenvu.ecommercems.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @TestConfiguration
    @EnableWebSecurity
    static class TestConfig {
        
        @Bean
        @Primary
        public AuthService mockAuthService() {
            return mock(AuthService.class);
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        reset(authService);
        
        validRegisterRequest = RegisterRequest.builder()
                .phoneNumber("0987654321")
                .password("Password123!")
                .build();

        validLoginRequest = LoginRequest.builder()
                .phoneNumber("0987654321")
                .password("Password123!")
                .build();
    }

    @Test
    void health_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("auth-service"));
    }

    @Test
    void register_ShouldReturn200_WhenValidRequest() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("User registered successfully");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void login_ShouldReturn200_WhenValidCredentials() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Map.of("token", "valid.jwt.token", "phoneNumber", "0987654321"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("valid.jwt.token"))
                .andExpect(jsonPath("$.phoneNumber").value("0987654321"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void register_ShouldReturn400_WhenInvalidPhoneNumber() throws Exception {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .phoneNumber("123456789")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isInternalServerError());

        verify(authService, never()).register(any(RegisterRequest.class));
    }
}
