package com.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    private String imageUrl;

    private String linkUrl;

    @Builder.Default
    private String type = "PROMOTION"; // 'PROMOTION' or 'EVENT'

    @Builder.Default
    private String status = "ACTIVE";
}
