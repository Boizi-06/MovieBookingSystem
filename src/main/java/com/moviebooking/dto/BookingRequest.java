package com.moviebooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    @NotNull(message = "Mã lịch chiếu không được để trống")
    private Long showtimeId;

    @NotEmpty(message = "Danh sách ghế chọn không được để trống")
    private Set<Long> seatIds;

    private java.util.List<ComboItemRequest> combos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComboItemRequest {
        private Long comboId;
        private Integer quantity;
    }
}
