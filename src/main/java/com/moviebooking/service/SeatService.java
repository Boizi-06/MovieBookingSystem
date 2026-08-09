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
     * Khởi tạo sơ đồ ghế mặc định cho phòng chiếu:
     * - Hàng A, B, C (24 ghế): Ghế Thường (STANDARD, hệ số giá 1.0)
     * - Hàng D, E (16 ghế): Ghế VIP (VIP, hệ số giá 1.2)
     * - Hàng F (4 ghế đôi): Ghế Đôi (SWEETBOX, hệ số giá 1.5)
     */
    @Transactional
    public List<Seat> generateDefaultSeats(Room room) {
        List<Seat> seats = new ArrayList<>();

        // 1. Hàng A, B, C: Ghế Thường (STANDARD) - 8 ghế mỗi hàng
        String[] standardRows = {"A", "B", "C"};
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

        // 2. Hàng D, E: Ghế VIP (VIP) - 8 ghế mỗi hàng
        String[] vipRows = {"D", "E"};
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

        // 3. Hàng F (Hàng cuối cùng): 4 ghế đôi (SWEETBOX)
        for (int i = 1; i <= 4; i++) {
            String seatCode = String.format("F%02d", i);
            Seat seat = Seat.builder()
                    .room(room)
                    .seatRow("F")
                    .seatNumber(i)
                    .seatCode(seatCode)
                    .seatType("SWEETBOX")
                    .priceMultiplier(new BigDecimal("1.50"))
                    .status("ACTIVE")
                    .build();
            seats.add(seat);
        }

        return seatRepository.saveAll(seats);
    }

    @Transactional
    public List<Seat> resetAndRegenerateSeatsForRoom(Room room) {
        seatRepository.deleteByRoomId(room.getId());
        return generateDefaultSeats(room);
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
