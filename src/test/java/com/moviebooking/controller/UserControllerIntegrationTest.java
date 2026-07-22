package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.UpdateProfileRequest;
import com.moviebooking.dto.UpdateUserStatusRequest;
import com.moviebooking.entity.User;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User customerUser;
    private String adminToken;
    private String customerToken;

    @BeforeEach
    public void setUp() {
        // Clear potential data conflicts, but we keep the seeded admin or create specific users
        // Let's create specific test users to isolate
        adminUser = User.builder()
                .fullname("Test Admin")
                .email("testadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Test Customer")
                .email("testcustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);
    }

    @Test
    public void testGetUserProfile_Success() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Lấy thông tin cá nhân thành công")))
                .andExpect(jsonPath("$.data.email", is("testcustomer@moviebooking.com")))
                .andExpect(jsonPath("$.data.fullname", is("Test Customer")))
                .andExpect(jsonPath("$.data.role", is("CUSTOMER")))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    public void testUpdateUserProfile_Success() throws Exception {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .fullname("Test Customer Updated")
                .phone("0987654321")
                .avatarUrl("http://example.com/avatar.png")
                .build();

        mockMvc.perform(put("/api/v1/users/profile")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Cập nhật thông tin cá nhân thành công")))
                .andExpect(jsonPath("$.data.fullname", is("Test Customer Updated")))
                .andExpect(jsonPath("$.data.phone", is("0987654321")))
                .andExpect(jsonPath("$.data.avatarUrl", is("http://example.com/avatar.png")));

        // Verify database state
        User updated = userRepository.findByEmail("testcustomer@moviebooking.com").orElseThrow();
        assertEquals("Test Customer Updated", updated.getFullname());
        assertEquals("0987654321", updated.getPhone());
    }

    @Test
    public void testSearchUsers_AdminSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", adminToken)
                        .param("keyword", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    public void testSearchUsers_ForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateUserStatus_AdminSuccess() throws Exception {
        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .status("LOCKED")
                .build();

        mockMvc.perform(put("/api/v1/users/" + customerUser.getId() + "/status")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("LOCKED")));

        // Verify status updated in DB
        User updated = userRepository.findById(customerUser.getId()).orElseThrow();
        assertEquals("LOCKED", updated.getStatus());
    }

    @Test
    public void testUpdateUserStatus_SelfLockPrevention() throws Exception {
        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .status("LOCKED")
                .build();

        // Admin trying to lock their own account
        mockMvc.perform(put("/api/v1/users/" + adminUser.getId() + "/status")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Bạn không thể tự khóa tài khoản của chính mình")));
    }
}
