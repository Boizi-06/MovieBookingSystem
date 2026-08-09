package com.moviebooking.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String bookingCode;
    private Long showtimeId;
    private String movieTitle;
    private String cinemaName;
    private String roomName;
    private LocalDateTime startTime;
    private List<String> seatCodes;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt; // Thời điểm hết hạn thanh toán (createdAt + 5 phút)
    private List<String> ticketCodes;
    private List<BookingComboResponse> comboItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingComboResponse {
        private Long comboId;
        private String comboName;
        private Integer quantity;
        private BigDecimal price;
    }
}
