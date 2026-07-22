package com.moviebooking.dto;

import com.moviebooking.entity.Seat;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {

    private Long id;
    private Long roomId;
    private String seatRow;
    private Integer seatNumber;
    private String seatCode;
    private String seatType;
    private BigDecimal priceMultiplier;
    private String status;

    public static SeatResponse fromEntity(Seat seat) {
        if (seat == null) return null;
        return SeatResponse.builder()
                .id(seat.getId())
                .roomId(seat.getRoom() != null ? seat.getRoom().getId() : null)
                .seatRow(seat.getSeatRow())
                .seatNumber(seat.getSeatNumber())
                .seatCode(seat.getSeatCode())
                .seatType(seat.getSeatType())
                .priceMultiplier(seat.getPriceMultiplier())
                .status(seat.getStatus())
                .build();
    }
}
