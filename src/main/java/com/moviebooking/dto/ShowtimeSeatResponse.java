package com.moviebooking.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeSeatResponse {
    private Long seatId;
    private String seatRow;
    private Integer seatNumber;
    private String seatCode;
    private String seatType;
    private BigDecimal price; // Giá vé thực tế = Giá cơ bản suất chiếu * priceMultiplier của ghế
    private String status;    // AVAILABLE, HOLD, BOOKED, MAINTENANCE
}
