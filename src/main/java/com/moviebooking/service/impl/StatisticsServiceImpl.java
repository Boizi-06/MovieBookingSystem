package com.moviebooking.service.impl;

import com.moviebooking.dto.MovieStatisticsResponse;
import com.moviebooking.dto.RevenueResponse;
import com.moviebooking.dto.TicketCountResponse;
import com.moviebooking.entity.Booking;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final BookingRepository bookingRepository;

    @Autowired
    public StatisticsServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueResponse> getRevenueStatistics(LocalDate startDate, LocalDate endDate, String groupBy) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (groupBy == null || groupBy.trim().isEmpty()) {
            groupBy = "DAY";
        }

        List<Booking> bookings = bookingRepository.findPaidBookingsWithDetails(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        Map<String, BigDecimal> revenueMap = new TreeMap<>();
        for (Booking booking : bookings) {
            String label = getGroupLabel(booking.getCreatedAt(), groupBy);
            BigDecimal current = revenueMap.getOrDefault(label, BigDecimal.ZERO);
            revenueMap.put(label, current.add(booking.getTotalPrice()));
        }

        List<RevenueResponse> responses = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : revenueMap.entrySet()) {
            responses.add(new RevenueResponse(entry.getKey(), entry.getValue()));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketCountResponse> getTicketStatistics(LocalDate startDate, LocalDate endDate, String groupBy) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (groupBy == null || groupBy.trim().isEmpty()) {
            groupBy = "DAY";
        }

        List<Booking> bookings = bookingRepository.findPaidBookingsWithDetails(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        Map<String, Long> ticketMap = new TreeMap<>();
        for (Booking booking : bookings) {
            String label = getGroupLabel(booking.getCreatedAt(), groupBy);
            Long current = ticketMap.getOrDefault(label, 0L);
            ticketMap.put(label, current + booking.getSeats().size());
        }

        List<TicketCountResponse> responses = new ArrayList<>();
        for (Map.Entry<String, Long> entry : ticketMap.entrySet()) {
            responses.add(new TicketCountResponse(entry.getKey(), entry.getValue()));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieStatisticsResponse> getMovieStatistics(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Booking> bookings = bookingRepository.findPaidBookingsWithDetails(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        Map<Long, MovieStatisticsResponse> movieMap = new HashMap<>();
        for (Booking booking : bookings) {
            com.moviebooking.entity.Movie movie = booking.getShowtime().getMovie();
            MovieStatisticsResponse stats = movieMap.get(movie.getId());
            if (stats == null) {
                stats = MovieStatisticsResponse.builder()
                        .movieId(movie.getId())
                        .movieTitle(movie.getTitle())
                        .ticketsSold(0L)
                        .revenue(BigDecimal.ZERO)
                        .build();
            }
            stats.setTicketsSold(stats.getTicketsSold() + booking.getSeats().size());
            stats.setRevenue(stats.getRevenue().add(booking.getTotalPrice()));
            movieMap.put(movie.getId(), stats);
        }

        List<MovieStatisticsResponse> responses = new ArrayList<>(movieMap.values());
        responses.sort((m1, m2) -> m2.getRevenue().compareTo(m1.getRevenue()));
        return responses;
    }

    private String getGroupLabel(LocalDateTime dateTime, String groupBy) {
        if ("MONTH".equalsIgnoreCase(groupBy)) {
            return String.format("%04d-%02d", dateTime.getYear(), dateTime.getMonthValue());
        } else if ("YEAR".equalsIgnoreCase(groupBy)) {
            return String.valueOf(dateTime.getYear());
        } else {
            return dateTime.toLocalDate().toString();
        }
    }
}
