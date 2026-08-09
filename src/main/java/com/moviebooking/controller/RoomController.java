package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.RoomRequest;
import com.moviebooking.dto.RoomResponse;
import com.moviebooking.service.RoomService;
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
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getRoomsByCinema(
            @RequestParam("cinemaId") Long cinemaId,
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
        Page<RoomResponse> roomPage = roomService.getRoomsByCinema(cinemaId, pageable);

        ApiResponse<Page<RoomResponse>> response = ApiResponse.<Page<RoomResponse>>builder()
                .success(true)
                .message("Lấy danh sách phòng chiếu thành công")
                .data(roomPage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable("id") Long id) {
        RoomResponse roomResponse = roomService.getRoomById(id);
        ApiResponse<RoomResponse> response = ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Lấy thông tin chi tiết phòng chiếu thành công")
                .data(roomResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse roomResponse = roomService.createRoom(request);
        ApiResponse<RoomResponse> response = ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Tạo phòng chiếu mới và sinh 60 ghế mặc định thành công")
                .data(roomResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse roomResponse = roomService.updateRoom(id, request);
        ApiResponse<RoomResponse> response = ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Cập nhật thông tin phòng chiếu thành công")
                .data(roomResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable("id") Long id) {
        String resultMsg = roomService.deleteRoom(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(resultMsg)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/reset-seats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> resetSeatsForRoom(@PathVariable("id") Long id) {
        RoomResponse roomResponse = roomService.resetSeatsForRoom(id);
        ApiResponse<RoomResponse> response = ApiResponse.<RoomResponse>builder()
                .success(true)
                .message("Tái tạo sơ đồ ghế mặc định cho phòng chiếu thành công")
                .data(roomResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
