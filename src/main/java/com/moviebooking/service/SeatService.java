package com.moviebooking.service;

import com.moviebooking.dto.SeatResponse;
import com.moviebooking.entity.Room;
import com.moviebooking.entity.Seat;
import com.moviebooking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    /**
     * Khởi tạo sơ đồ 60 ghế mặc định cho phòng chiếu:
     * - Hàng A-E (40 ghế): Ghế Thường (STANDARD, hệ số giá 1.0)
     * - Hàng F-G (16 ghế): Ghế VIP (VIP, hệ số giá 1.2)
     * - Hàng H (4 ghế đôi): Ghế Đôi (COUPLE, hệ số giá 1.5)
     */
    @Transactional
    public List<Seat> generateDefaultSeats(Room room) {
        List<Seat> seats = new ArrayList<>();

        // 1. Hàng A đến E: Ghế Thường (STANDARD) - 8 ghế mỗi hàng
        String[] standardRows = {"A", "B", "C", "D", "E"};
        for (String row : standardRows) {
            for (int i = 1; i <= 8; i++) {
                String seatCode = String.format("%s%02d", row, i);
                Seat seat = Seat.builder()
                        .room(room)
                        .seatRow(row)
                        .seatNumber(i)
                        .seatCode(seatCode)
                        .seatType("STANDARD")
                        .priceMultiplier(new BigDecimal("1.00"))
                        .status("ACTIVE")
                        .build();
                seats.add(seat);
            }
        }

        // 2. Hàng F đến G: Ghế VIP (VIP) - 8 ghế mỗi hàng
        String[] vipRows = {"F", "G"};
        for (String row : vipRows) {
            for (int i = 1; i <= 8; i++) {
                String seatCode = String.format("%s%02d", row, i);
                Seat seat = Seat.builder()
                        .room(room)
                        .seatRow(row)
                        .seatNumber(i)
                        .seatCode(seatCode)
                        .seatType("VIP")
                        .priceMultiplier(new BigDecimal("1.20"))
                        .status("ACTIVE")
                        .build();
                seats.add(seat);
            }
        }

        // 3. Hàng H: Ghế Đôi (COUPLE) - 4 ghế đôi
        for (int i = 1; i <= 4; i++) {
            String seatCode = String.format("H%02d", i);
            Seat seat = Seat.builder()
                    .room(room)
                    .seatRow("H")
                    .seatNumber(i)
                    .seatCode(seatCode)
                    .seatType("COUPLE")
                    .priceMultiplier(new BigDecimal("1.50"))
                    .status("ACTIVE")
                    .build();
            seats.add(seat);
        }

        return seatRepository.saveAll(seats);
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsByRoomId(Long roomId) {
        List<Seat> seats = seatRepository.findByRoomIdOrderBySeatRowAscSeatNumberAsc(roomId);
        return seats.stream().map(SeatResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public SeatResponse updateSeatStatus(Long seatId, String status) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ghế với ID: " + seatId));

        seat.setStatus(status.trim().toUpperCase());
        Seat updatedSeat = seatRepository.save(seat);
        return SeatResponse.fromEntity(updatedSeat);
    }
}
