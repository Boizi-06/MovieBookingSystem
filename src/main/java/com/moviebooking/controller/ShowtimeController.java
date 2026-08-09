package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.ShowtimeRequest;
import com.moviebooking.dto.ShowtimeResponse;
import com.moviebooking.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @Autowired
    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .findFirst()
                    .orElse("GUEST");
        }
        return "GUEST";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> createShowtime(@Valid @RequestBody ShowtimeRequest request) {
        ShowtimeResponse showtimeResponse = showtimeService.createShowtime(request);
        ApiResponse<ShowtimeResponse> response = ApiResponse.<ShowtimeResponse>builder()
                .success(true)
                .message("Thêm lịch chiếu phim mới thành công")
                .data(showtimeResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> searchShowtimes(
            @RequestParam(value = "movieId", required = false) Long movieId,
            @RequestParam(value = "cinemaId", required = false) Long cinemaId,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "status", required = false) String status) {
        String userRole = getCurrentUserRole();
        List<ShowtimeResponse> showtimes = showtimeService.searchShowtimes(movieId, cinemaId, date, status, userRole);
        ApiResponse<List<ShowtimeResponse>> response = ApiResponse.<List<ShowtimeResponse>>builder()
                .success(true)
                .message("Lấy danh sách lịch chiếu thành công")
                .data(showtimes)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getShowtimeById(@PathVariable("id") Long id) {
        ShowtimeResponse showtimeResponse = showtimeService.getShowtimeById(id);
        ApiResponse<ShowtimeResponse> response = ApiResponse.<ShowtimeResponse>builder()
                .success(true)
                .message("Lấy chi tiết lịch chiếu thành công")
                .data(showtimeResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> updateShowtime(
            @PathVariable("id") Long id,
            @Valid @RequestBody ShowtimeRequest request) {
        ShowtimeResponse showtimeResponse = showtimeService.updateShowtime(id, request);
        ApiResponse<ShowtimeResponse> response = ApiResponse.<ShowtimeResponse>builder()
                .success(true)
                .message("Cập nhật lịch chiếu phim thành công")
                .data(showtimeResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShowtime(@PathVariable("id") Long id) {
        String message = showtimeService.deleteShowtime(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
