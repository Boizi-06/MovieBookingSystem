package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.FoodComboRequest;
import com.moviebooking.dto.FoodComboResponse;
import com.moviebooking.service.FoodComboService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/combos")
public class FoodComboController {

    private final FoodComboService foodComboService;

    @Autowired
    public FoodComboController(FoodComboService foodComboService) {
        this.foodComboService = foodComboService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodComboResponse>>> getActiveCombos() {
        List<FoodComboResponse> combos = foodComboService.getActiveCombos();
        ApiResponse<List<FoodComboResponse>> response = ApiResponse.<List<FoodComboResponse>>builder()
                .success(true)
                .message("Lấy danh sách Combo bỏng nước thành công")
                .data(combos)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<FoodComboResponse>>> getAllCombosAdmin() {
        List<FoodComboResponse> combos = foodComboService.getAllCombosAdmin();
        ApiResponse<List<FoodComboResponse>> response = ApiResponse.<List<FoodComboResponse>>builder()
                .success(true)
                .message("Lấy danh sách tất cả Combo bỏng nước quản trị thành công")
                .data(combos)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FoodComboResponse>> createCombo(@Valid @RequestBody FoodComboRequest request) {
        FoodComboResponse combo = foodComboService.createCombo(request);
        ApiResponse<FoodComboResponse> response = ApiResponse.<FoodComboResponse>builder()
                .success(true)
                .message("Tạo Combo bỏng nước mới thành công")
                .data(combo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FoodComboResponse>> updateCombo(@PathVariable("id") Long id, @Valid @RequestBody FoodComboRequest request) {
        FoodComboResponse combo = foodComboService.updateCombo(id, request);
        ApiResponse<FoodComboResponse> response = ApiResponse.<FoodComboResponse>builder()
                .success(true)
                .message("Cập nhật Combo bỏng nước thành công")
                .data(combo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCombo(@PathVariable("id") Long id) {
        foodComboService.deleteCombo(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa Combo bỏng nước thành công")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
