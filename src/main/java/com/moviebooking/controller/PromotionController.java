package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.PromotionRequest;
import com.moviebooking.dto.PromotionResponse;
import com.moviebooking.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getPromotions(@RequestParam(name = "type", required = false) String type) {
        List<PromotionResponse> promotions = promotionService.getPromotionsByType(type);
        ApiResponse<List<PromotionResponse>> response = ApiResponse.<List<PromotionResponse>>builder()
                .success(true)
                .message("Lấy danh sách khuyến mãi thành công")
                .data(promotions)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PromotionResponse>>> getAllPromotionsAdmin() {
        List<PromotionResponse> promotions = promotionService.getAllPromotionsAdmin();
        ApiResponse<List<PromotionResponse>> response = ApiResponse.<List<PromotionResponse>>builder()
                .success(true)
                .message("Lấy danh sách tất cả khuyến mãi quản trị thành công")
                .data(promotions)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(@Valid @RequestBody PromotionRequest request) {
        PromotionResponse promotion = promotionService.createPromotion(request);
        ApiResponse<PromotionResponse> response = ApiResponse.<PromotionResponse>builder()
                .success(true)
                .message("Tạo chương trình khuyến mãi / sự kiện mới thành công")
                .data(promotion)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponse>> updatePromotion(@PathVariable("id") Long id, @Valid @RequestBody PromotionRequest request) {
        PromotionResponse promotion = promotionService.updatePromotion(id, request);
        ApiResponse<PromotionResponse> response = ApiResponse.<PromotionResponse>builder()
                .success(true)
                .message("Cập nhật khuyến mãi / sự kiện thành công")
                .data(promotion)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable("id") Long id) {
        promotionService.deletePromotion(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa khuyến mãi / sự kiện thành công")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
