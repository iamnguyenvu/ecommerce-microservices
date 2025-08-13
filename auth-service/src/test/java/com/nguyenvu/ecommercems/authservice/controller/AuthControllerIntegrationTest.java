package com.nguyenvu.ecommercems.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenvu.ecommercems.authservice.dto.LoginRequest;
import com.nguyenvu.ecommercems.authservice.dto.RegisterRequest;
import com.nguyenvu.ecommercems.authservice.model.User;
import com.nguyenvu.ecommercems.authservice.model.UserRole;
import com.nguyenvu.ecommercems.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController
 * 
 * These tests run against the full Spring context with:
 * - Real database (H2 in-memory for testing)
 * - Real service layer
 * - Real security configuration
 * - Real JPA repositories
 * 
 * Use @Transactional to rollback data changes after each test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:integrationtestdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    // Test data
    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        // Setup MockMvc with full Spring context
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        
        // Clean database before each test
        userRepository.deleteAll();
        
        // Prepare test data with Vietnamese phone numbers
        validRegisterRequest = RegisterRequest.builder()
                .phoneNumber("0987654321")
                .password("NewUser123!")
                .build();
                
        validLoginRequest = LoginRequest.builder()
                .phoneNumber("0912345678")
                .password("ExistingUser123!")
                .build();
        
        // Create an existing user in database for login tests
        existingUser = User.builder()
                .phoneNumber("0912345678")
                .password(passwordEncoder.encode("ExistingUser123!"))
                .role(UserRole.USER)
                .phoneVerified(false)
                .build();
        userRepository.save(existingUser);
    }

    // ============== HEALTH ENDPOINT TESTS ==============
    
    @Test
    void health_ShouldReturn200WithCorrectResponse() throws Exception {
        mockMvc.perform(get("/auth/health"))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("auth-service"));
    }

    // ============== REGISTRATION TESTS ==============
    
    /**
     * COMPLETE EXAMPLE: Full integration test for user registration
     * 
     * This test demonstrates:
     * - Real HTTP request through MockMvc
     * - Real service layer processing
     * - Real database interaction
     * - Real password encoding
     * - Verification of database state
     */
    @Test
    void register_ShouldCreateUserInDatabase_WhenValidRequest() throws Exception {
        // Given: Valid registration request
        String requestBody = objectMapper.writeValueAsString(validRegisterRequest);
        
        // When: POST request to registration endpoint
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
//                .andDo(print()) // Print request/response for debugging
                // Then: Expect successful response
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("successfully")));
        
        // Verify: User is actually saved in database with phone number
        User savedUser = userRepository.findByPhoneNumber("0987654321").orElse(null);
        assertNotNull(savedUser, "User should be saved in database");
        assertEquals("0987654321", savedUser.getPhoneNumber());
        assertEquals(false, savedUser.getPhoneVerified()); // Default should be false
        assertTrue(passwordEncoder.matches("NewUser123!", savedUser.getPassword()), 
                  "Password should be encoded correctly");
        
        // Verify: Database state
        assertEquals(2, userRepository.count(), "Should have 2 users total (existing + new)");
    }
    
    @Test
    void register_ShouldReturn400_WhenPhoneNumberAlreadyExists() throws Exception {
        // TODO: Implement test for duplicate phone number
        // Steps:
        // 1. Try to register with phone number "0912345678" (already in DB)
        RegisterRequest duplicatePhoneRequest = createRegisterRequest("0912345678", "AnotherPassword123!");
        // 2. Expect 400 status
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatePhoneRequest)))
//                .andDo(print())
                .andExpect(status().isBadRequest());

        // 3. Verify error message
        assertUserExistsInDatabase(duplicatePhoneRequest.getPhoneNumber());
        // 4. Verify no new user created in DB
        assertEquals(1, userRepository.count(), "Should still have 1 users in database");
    }
    
    @Test
    void register_ShouldReturn400_WhenPasswordTooWeak() throws Exception {
        // Steps:
        // 1. Create RegisterRequest with weak password (e.g., "123")
        RegisterRequest weakPasswordRequest = createRegisterRequest("0888888888", "123");
        // 2. Send POST request
        // 3. Expect 400 status (or 500 based on GlobalExceptionHandler)
        // 4. Verify validation error message
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("An unexpected error occurred"));

        // 5. Verify no user created in DB
        assertEquals(1, userRepository.count(), "Should still have 1 users in database");
    }
    
    @Test
    void register_ShouldReturn400_WhenPhoneNumberNull() throws Exception {
        RegisterRequest nullPhoneRequest = createRegisterRequest(null, "ValidPassword123!");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullPhoneRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));

        assertEquals(1, userRepository.count(), "Should still have 1 users in database");
    }

    // ============== LOGIN TESTS ==============
    
    @Test
    void login_ShouldReturnJWTToken_WhenValidCredentials() throws Exception {
        // TODO: Implement test for successful login
        // Steps:
        // 1. Use validLoginRequest (user exists in DB from setUp)
        validLoginRequest = createLoginRequest(existingUser.getPhoneNumber(), "ExistingUser123!");
        // 2. Send POST request to /auth/login
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
        // 3. Expect 200 status
                .andExpect(status().isOk())
        // 4. Verify JWT token in response
                .andExpect(jsonPath("$.token").exists());
        // 5. Optionally validate token structure
    }
    
    @Test
    void login_ShouldReturn401_WhenInvalidPassword() throws Exception {
        // TODO: Implement test for wrong password
        // Steps:
        // 1. Create LoginRequest with correct phone number but wrong password
        LoginRequest wrongPasswordRequest = createLoginRequest("0912345678", "WrongPassword123!");
        // 2. Send POST request
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
//                .andDo(print())
        // 3. Expect 401 status (or 400 based on GlobalExceptionHandler)
                .andExpect(status().isBadRequest())
        // 4. Verify error message
                .andExpect(jsonPath("$.error").value("Invalid password"));
    }
    
    @Test
    void login_ShouldReturn404_WhenUserNotFound() throws Exception {
        // TODO: Implement test for non-existent user
        // Steps:
        // 1. Create LoginRequest with non-existent phone number
        LoginRequest wrongPasswordRequest = createLoginRequest("0999888777", "AnotherPassword123!");
        // 2. Send POST request
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andDo(print())
        // 3. Expect 404 status (or 400 based on GlobalExceptionHandler)
                .andExpect(status().isBadRequest())
        // 4. Verify error message
                .andExpect(jsonPath("$.error").value("Phone number not found"));
    }
    
    @Test
    void login_ShouldReturn400_WhenPasswordNull() throws Exception {
        // TODO: Implement test for null password validation
        LoginRequest nullPasswordRequest = createLoginRequest("existinguser", null);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullPasswordRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));
    }

    // ============== VALIDATION TESTS ==============
    
    @Test
    void register_ShouldReturn400_WhenPhoneNumberTooShort() throws Exception {
        // TODO: Implement test for phone number length validation (@Size annotation)
        RegisterRequest shortPhoneRequest = createRegisterRequest("098765432", "ValidPassword123!"); // 9 digits - invalid

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortPhoneRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));

        assertEquals(1, userRepository.count(), "Should still have 1 users in database");
    }
    
    @Test
    void register_ShouldReturn400_WhenPhoneNumberTooLong() throws Exception {
        // TODO: Implement test for phone number max length validation
        RegisterRequest longPhoneRequest = createRegisterRequest("01234567890", "ValidPassword123!"); // 11 digits - invalid
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longPhoneRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occurred"));
    }
    
    @Test
    void register_ShouldReturn400_WhenPasswordDoesNotMeetComplexity() throws Exception {
        // TODO: Implement test for password pattern validation
        // Test cases:
        // - No uppercase letter
        // - No lowercase letter  
        // - No digit
        // - No special character
        // - Too short (< 8 characters)
        // - Too long (> 20 characters)
    }

    // ============== CONTENT TYPE TESTS ==============
    
    @Test
    void register_ShouldReturn415_WhenWrongContentType() throws Exception {
        // TODO: Implement test for unsupported media type
        // Steps:
        // 1. Send request with text/plain content type
        // 2. Expect 415 status (or 500 based on GlobalExceptionHandler)
    }

    // ============== DATABASE TRANSACTION TESTS ==============
    
    @Test
    void register_ShouldRollbackTransaction_WhenDatabaseError() throws Exception {
        // TODO: Implement test for transaction rollback
        // This requires simulating a database error
        // Consider using @Sql or custom configuration
    }

    // ============== PERFORMANCE TESTS ==============
    
    @Test
    void register_ShouldCompleteWithinReasonableTime() throws Exception {
        // TODO: Implement performance test
        // Steps:
        // 1. Record start time
        // 2. Send registration request
        // 3. Record end time
        // 4. Assert response time < threshold (e.g., 1000ms)
    }

    // ============== CONCURRENT ACCESS TESTS ==============
    
    @Test
    void register_ShouldHandleConcurrentRequests() throws Exception {
        // TODO: Implement concurrency test
        // Steps:
        // 1. Create multiple threads
        // 2. Each thread tries to register same phone number
        // 3. Only one should succeed
        // 4. Others should get appropriate error
    }

    // ============== HELPER METHODS ==============
    
    /**
     * Helper method to create RegisterRequest with custom values
     */
    private RegisterRequest createRegisterRequest(String phoneNumber, String password) {
        return RegisterRequest.builder()
                .phoneNumber(phoneNumber)
                .password(password)
                .build();
    }
    
    /**
     * Helper method to create LoginRequest with custom values
     */
    private LoginRequest createLoginRequest(String phoneNumber, String password) {
        return LoginRequest.builder()
                .phoneNumber(phoneNumber)
                .password(password)
                .build();
    }
    
    /**
     * Helper method to create and save user in database
     */
    private User createAndSaveUser(String phoneNumber, String rawPassword) {
        User user = User.builder()
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(rawPassword))
                .role(UserRole.USER)
                .phoneVerified(false)
                .build();
        return userRepository.save(user);
    }
    
    /**
     * Helper method to verify user exists in database
     */
    private void assertUserExistsInDatabase(String phoneNumber) {
        assertTrue(userRepository.findByPhoneNumber(phoneNumber).isPresent(), 
                  "User '" + phoneNumber + "' should exist in database");
    }
    
    /**
     * Helper method to verify user does not exist in database
     */
    private void assertUserNotExistsInDatabase(String phoneNumber) {
        assertFalse(userRepository.findByPhoneNumber(phoneNumber).isPresent(), 
                   "User '" + phoneNumber + "' should not exist in database");
    }
}
