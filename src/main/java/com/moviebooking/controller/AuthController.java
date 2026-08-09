package com.moviebooking.controller;

import com.moviebooking.dto.*;
import com.moviebooking.entity.User;
import com.moviebooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User registeredUser = authService.register(request);
        ApiResponse<User> response = ApiResponse.<User>builder()
                .success(true)
                .message("Đăng ký tài khoản thành công. Vui lòng kích hoạt tài khoản qua liên kết trong email gửi tới bạn.")
                .data(registeredUser)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Đăng nhập thành công")
                .data(loginResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(request);
        ApiResponse<TokenRefreshResponse> response = ApiResponse.<TokenRefreshResponse>builder()
                .success(true)
                .message("Làm mới token thành công")
                .data(tokenRefreshResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Yêu cầu khôi phục mật khẩu đã được tiếp nhận. Vui lòng kiểm tra email của bạn.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Mật khẩu đã được thay đổi thành công.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Tài khoản đã được kích hoạt thành công. Bạn đã có thể đăng nhập.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            java.security.Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        String email = principal.getName();
        authService.changePassword(email, request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Đổi mật khẩu thành công.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
