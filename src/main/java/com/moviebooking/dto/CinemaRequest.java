package com.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaRequest {

    @NotBlank(message = "Tên rạp không được để trống")
    @Size(max = 100, message = "Tên rạp không được vượt quá 100 ký tự")
    private String name;

    @NotBlank(message = "Địa chỉ rạp không được để trống")
    @Size(max = 255, message = "Địa chỉ rạp không được vượt quá 255 ký tự")
    private String address;

    @NotBlank(message = "Thành phố không được để trống")
    @Size(max = 100, message = "Tên thành phố không được vượt quá 100 ký tự")
    private String city;

    @Pattern(regexp = "^$|^[0-9]{10,11}$", message = "Số điện thoại rạp không hợp lệ (phải gồm 10-11 chữ số)")
    private String phone;

    @Pattern(regexp = "^$|^(ACTIVE|INACTIVE)$", message = "Trạng thái rạp không hợp lệ (chỉ nhận ACTIVE hoặc INACTIVE)")
    private String status;
}
