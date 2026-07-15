package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestSecurityController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> testPublic() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("API Public: Ai cũng có thể truy cập thành công.")
                .data("Dữ liệu công khai")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customer-only")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> testCustomerOnly(@AuthenticationPrincipal String email) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("API Customer: Chỉ vai trò CUSTOMER mới truy cập được.")
                .data("Đăng nhập email: " + email)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> testAdminOnly(@AuthenticationPrincipal String email) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("API Admin: Chỉ vai trò ADMIN mới truy cập được.")
                .data("Đăng nhập email: " + email)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/any-role")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> testAnyRole(@AuthenticationPrincipal String email) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("API Any Role: Có token hợp lệ là có thể truy cập.")
                .data("Đăng nhập email: " + email)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
