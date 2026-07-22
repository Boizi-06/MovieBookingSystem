package com.moviebooking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được dài quá 100 ký tự")
    private String fullname;

    @Pattern(regexp = "^$|^(0[3|5|7|8|9])+([0-9]{8})\\b$", message = "Số điện thoại không đúng định dạng Việt Nam (10 chữ số)")
    private String phone;

    private String avatarUrl;
}
