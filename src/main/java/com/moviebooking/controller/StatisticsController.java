package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.MovieStatisticsResponse;
import com.moviebooking.dto.RevenueResponse;
import com.moviebooking.dto.TicketCountResponse;
import com.moviebooking.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<List<RevenueResponse>>> getRevenueStatistics(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "groupBy", required = false, defaultValue = "DAY") String groupBy) {

        List<RevenueResponse> statistics = statisticsService.getRevenueStatistics(startDate, endDate, groupBy);
        ApiResponse<List<RevenueResponse>> response = ApiResponse.<List<RevenueResponse>>builder()
                .success(true)
                .message("Lấy thống kê doanh thu thành công")
                .data(statistics)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<TicketCountResponse>>> getTicketStatistics(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "groupBy", required = false, defaultValue = "DAY") String groupBy) {

        List<TicketCountResponse> statistics = statisticsService.getTicketStatistics(startDate, endDate, groupBy);
        ApiResponse<List<TicketCountResponse>> response = ApiResponse.<List<TicketCountResponse>>builder()
                .success(true)
                .message("Lấy thống kê số lượng vé bán ra thành công")
                .data(statistics)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<MovieStatisticsResponse>>> getMovieStatistics(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {

        List<MovieStatisticsResponse> statistics = statisticsService.getMovieStatistics(startDate, endDate);
        ApiResponse<List<MovieStatisticsResponse>> response = ApiResponse.<List<MovieStatisticsResponse>>builder()
                .success(true)
                .message("Lấy thống kê hiệu suất phim thành công")
                .data(statistics)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
