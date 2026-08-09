package com.moviebooking.dto;

import com.moviebooking.entity.Showtime;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeResponse {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private Integer movieDuration;
    private Long cinemaId;
    private String cinemaName;
    private Long roomId;
    private String roomName;
    private String roomType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShowtimeResponse fromEntity(Showtime showtime) {
        if (showtime == null) return null;
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie() != null ? showtime.getMovie().getId() : null)
                .movieTitle(showtime.getMovie() != null ? showtime.getMovie().getTitle() : null)
                .moviePosterUrl(showtime.getMovie() != null ? showtime.getMovie().getPosterUrl() : null)
                .movieDuration(showtime.getMovie() != null ? showtime.getMovie().getDuration() : null)
                .cinemaId(showtime.getRoom() != null && showtime.getRoom().getCinema() != null ? showtime.getRoom().getCinema().getId() : null)
                .cinemaName(showtime.getRoom() != null && showtime.getRoom().getCinema() != null ? showtime.getRoom().getCinema().getName() : null)
                .roomId(showtime.getRoom() != null ? showtime.getRoom().getId() : null)
                .roomName(showtime.getRoom() != null ? showtime.getRoom().getName() : null)
                .roomType(showtime.getRoom() != null ? showtime.getRoom().getRoomType() : null)
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .basePrice(showtime.getBasePrice())
                .status(showtime.getStatus())
                .createdAt(showtime.getCreatedAt())
                .updatedAt(showtime.getUpdatedAt())
                .build();
    }
}
