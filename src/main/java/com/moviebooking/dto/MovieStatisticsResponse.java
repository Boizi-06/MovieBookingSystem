package com.moviebooking.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieStatisticsResponse {
    private Long movieId;
    private String movieTitle;
    private Long ticketsSold;
    private BigDecimal revenue;
}
