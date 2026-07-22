package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.SeatResponse;
import com.moviebooking.dto.SeatStatusUpdateRequest;
import com.moviebooking.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
public class SeatController {

    private final SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsByRoomId(@PathVariable("roomId") Long roomId) {
        List<SeatResponse> seats = seatService.getSeatsByRoomId(roomId);
        ApiResponse<List<SeatResponse>> response = ApiResponse.<List<SeatResponse>>builder()
                .success(true)
                .message("Lấy sơ đồ danh sách ghế thành công")
                .data(seats)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SeatResponse>> updateSeatStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody SeatStatusUpdateRequest request) {
        SeatResponse seatResponse = seatService.updateSeatStatus(id, request.getStatus());
        ApiResponse<SeatResponse> response = ApiResponse.<SeatResponse>builder()
                .success(true)
                .message("Cập nhật trạng thái ghế thành công")
                .data(seatResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
