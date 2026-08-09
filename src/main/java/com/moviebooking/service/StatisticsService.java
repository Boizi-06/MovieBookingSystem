package com.moviebooking.service;

import com.moviebooking.dto.MovieStatisticsResponse;
import com.moviebooking.dto.RevenueResponse;
import com.moviebooking.dto.TicketCountResponse;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    List<RevenueResponse> getRevenueStatistics(LocalDate startDate, LocalDate endDate, String groupBy);
    List<TicketCountResponse> getTicketStatistics(LocalDate startDate, LocalDate endDate, String groupBy);
    List<MovieStatisticsResponse> getMovieStatistics(LocalDate startDate, LocalDate endDate);
}
