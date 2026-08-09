package com.moviebooking.dto;

import com.moviebooking.entity.Promotion;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private String type;
    private String status;
    private LocalDateTime createdAt;

    public static PromotionResponse fromEntity(Promotion p) {
        if (p == null) return null;
        return PromotionResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .imageUrl(p.getImageUrl())
                .linkUrl(p.getLinkUrl())
                .type(p.getType())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
