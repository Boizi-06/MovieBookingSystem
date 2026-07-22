package com.moviebooking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserStatusRequest {

    @NotBlank(message = "Trạng thái không được để trống")
    @Pattern(regexp = "^(ACTIVE|LOCKED)$", message = "Trạng thái chỉ có thể là ACTIVE hoặc LOCKED")
    private String status;
}
