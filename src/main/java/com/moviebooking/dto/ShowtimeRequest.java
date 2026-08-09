package com.moviebooking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeRequest {

    @NotNull(message = "Mã phim không được để trống")
    private Long movieId;

    @NotNull(message = "Mã phòng chiếu không được để trống")
    private Long roomId;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull(message = "Giá vé cơ bản không được để trống")
    @DecimalMin(value = "0.00", message = "Giá vé cơ bản phải lớn hơn hoặc bằng 0")
    private BigDecimal basePrice;

    private String status;
}
