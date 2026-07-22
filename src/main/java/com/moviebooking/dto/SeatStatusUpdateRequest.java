package com.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatStatusUpdateRequest {

    @NotBlank(message = "Trạng thái ghế không được để trống")
    @Pattern(regexp = "^(ACTIVE|MAINTENANCE|INACTIVE)$", message = "Trạng thái ghế không hợp lệ (chỉ nhận ACTIVE, MAINTENANCE, INACTIVE)")
    private String status;
}
