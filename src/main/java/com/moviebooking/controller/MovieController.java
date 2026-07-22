package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.MovieRequest;
import com.moviebooking.dto.MovieResponse;
import com.moviebooking.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getMovies(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "genreName", required = false) String genreName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,desc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = Sort.Direction.DESC;
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        String userRole = getCurrentUserRole();
        Page<MovieResponse> moviePage = movieService.getMovies(keyword, genreId, genreName, status, userRole, pageable);

        ApiResponse<Page<MovieResponse>> response = ApiResponse.<Page<MovieResponse>>builder()
                .success(true)
                .message("Lấy danh sách phim thành công")
                .data(moviePage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable("id") Long id) {
        String userRole = getCurrentUserRole();
        MovieResponse movieResponse = movieService.getMovieById(id, userRole);
        ApiResponse<MovieResponse> response = ApiResponse.<MovieResponse>builder()
                .success(true)
                .message("Lấy thông tin chi tiết phim thành công")
                .data(movieResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse movieResponse = movieService.createMovie(request);
        ApiResponse<MovieResponse> response = ApiResponse.<MovieResponse>builder()
                .success(true)
                .message("Thêm bộ phim mới thành công")
                .data(movieResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable("id") Long id,
            @Valid @RequestBody MovieRequest request) {
        MovieResponse movieResponse = movieService.updateMovie(id, request);
        ApiResponse<MovieResponse> response = ApiResponse.<MovieResponse>builder()
                .success(true)
                .message("Cập nhật thông tin phim thành công")
                .data(movieResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable("id") Long id) {
        String resultMsg = movieService.deleteMovie(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(resultMsg)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
