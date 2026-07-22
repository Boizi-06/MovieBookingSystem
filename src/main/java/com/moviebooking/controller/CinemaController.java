package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.CinemaRequest;
import com.moviebooking.dto.CinemaResponse;
import com.moviebooking.service.CinemaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;

    @Autowired
    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<CinemaResponse>>> getAllCinemas(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        org.springframework.data.domain.Sort.Direction direction = org.springframework.data.domain.Sort.Direction.ASC;
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            direction = org.springframework.data.domain.Sort.Direction.DESC;
        }

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(direction, sortField));
        org.springframework.data.domain.Page<CinemaResponse> cinemaPage = cinemaService.getAllCinemas(keyword, city, pageable);

        ApiResponse<org.springframework.data.domain.Page<CinemaResponse>> response = ApiResponse.<org.springframework.data.domain.Page<CinemaResponse>>builder()
                .success(true)
                .message("Lấy danh sách rạp chiếu phim thành công")
                .data(cinemaPage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CinemaResponse>> getCinemaById(@PathVariable("id") Long id) {
        CinemaResponse cinemaResponse = cinemaService.getCinemaById(id);
        ApiResponse<CinemaResponse> response = ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Lấy thông tin chi tiết rạp chiếu phim thành công")
                .data(cinemaResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CinemaResponse>> createCinema(@Valid @RequestBody CinemaRequest request) {
        CinemaResponse cinemaResponse = cinemaService.createCinema(request);
        ApiResponse<CinemaResponse> response = ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Thêm rạp chiếu phim mới thành công")
                .data(cinemaResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CinemaResponse>> updateCinema(
            @PathVariable("id") Long id,
            @Valid @RequestBody CinemaRequest request) {
        CinemaResponse cinemaResponse = cinemaService.updateCinema(id, request);
        ApiResponse<CinemaResponse> response = ApiResponse.<CinemaResponse>builder()
                .success(true)
                .message("Cập nhật thông tin rạp chiếu phim thành công")
                .data(cinemaResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCinema(@PathVariable("id") Long id) {
        String resultMsg = cinemaService.deleteCinema(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(resultMsg)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
