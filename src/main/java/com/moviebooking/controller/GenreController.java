package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.GenreRequest;
import com.moviebooking.dto.GenreResponse;
import com.moviebooking.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GenreResponse>>> getAllGenres(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<GenreResponse> genrePage = genreService.getAllGenres(keyword, pageable);

        ApiResponse<Page<GenreResponse>> response = ApiResponse.<Page<GenreResponse>>builder()
                .success(true)
                .message("Lấy danh sách thể loại phim thành công")
                .data(genrePage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreById(@PathVariable("id") Long id) {
        GenreResponse genreResponse = genreService.getGenreById(id);
        ApiResponse<GenreResponse> response = ApiResponse.<GenreResponse>builder()
                .success(true)
                .message("Lấy thông tin chi tiết thể loại thành công")
                .data(genreResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GenreResponse>> createGenre(@Valid @RequestBody GenreRequest request) {
        GenreResponse genreResponse = genreService.createGenre(request);
        ApiResponse<GenreResponse> response = ApiResponse.<GenreResponse>builder()
                .success(true)
                .message("Thêm thể loại phim mới thành công")
                .data(genreResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GenreResponse>> updateGenre(
            @PathVariable("id") Long id,
            @Valid @RequestBody GenreRequest request) {
        GenreResponse genreResponse = genreService.updateGenre(id, request);
        ApiResponse<GenreResponse> response = ApiResponse.<GenreResponse>builder()
                .success(true)
                .message("Cập nhật thông tin thể loại phim thành công")
                .data(genreResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable("id") Long id) {
        genreService.deleteGenre(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa thể loại phim thành công")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
