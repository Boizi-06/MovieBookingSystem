package com.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {

    @NotNull(message = "Mã rạp chiếu phim không được để trống")
    private Long cinemaId;

    @NotBlank(message = "Tên phòng chiếu không được để trống")
    @Size(max = 50, message = "Tên phòng chiếu không được vượt quá 50 ký tự")
    private String name;

    @Pattern(regexp = "^$|^(STANDARD|VIP|IMAX)$", message = "Loại phòng chiếu không hợp lệ (chỉ nhận STANDARD, VIP, IMAX)")
    private String roomType;

    @Pattern(regexp = "^$|^(ACTIVE|MAINTENANCE|INACTIVE)$", message = "Trạng thái phòng chiếu không hợp lệ (chỉ nhận ACTIVE, MAINTENANCE, INACTIVE)")
    private String status;
}
