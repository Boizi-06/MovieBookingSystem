package com.moviebooking.dto;

import com.moviebooking.entity.FoodCombo;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodComboResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;

    public static FoodComboResponse fromEntity(FoodCombo f) {
        if (f == null) return null;
        return FoodComboResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .price(f.getPrice())
                .imageUrl(f.getImageUrl())
                .status(f.getStatus())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
