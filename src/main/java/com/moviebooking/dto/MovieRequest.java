package com.moviebooking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRequest {

    @NotBlank(message = "Tên phim không được để trống")
    @Size(max = 255, message = "Tên phim không được vượt quá 255 ký tự")
    private String title;

    private String description;

    @NotNull(message = "Thời lượng phim không được để trống")
    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0 phút")
    private Integer duration;

    @NotNull(message = "Ngày khởi chiếu không được để trống")
    private LocalDate releaseDate;

    private LocalDate endDate;

    @NotBlank(message = "Độ tuổi không được để trống")
    @Size(max = 20, message = "Mã độ tuổi không được vượt quá 20 ký tự")
    private String ageRating; // "P", "C13", "C16", "C18"

    @NotBlank(message = "Ngôn ngữ không được để trống")
    @Size(max = 100, message = "Ngôn ngữ không được vượt quá 100 ký tự")
    private String language;

    private String director;

    private String cast;

    private String posterUrl;

    private String bannerUrl;

    private String trailerUrl;

    @Pattern(regexp = "^(UPCOMING|NOW_SHOWING|ENDED|INACTIVE)$", message = "Trạng thái phim không hợp lệ")
    private String status;

    @NotEmpty(message = "Mỗi phim phải thuộc ít nhất một thể loại")
    private Set<Long> genreIds;
}
