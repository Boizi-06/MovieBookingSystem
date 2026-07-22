package com.moviebooking.repository;

import com.moviebooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByRoomIdOrderBySeatRowAscSeatNumberAsc(Long roomId);
    boolean existsByRoomIdAndSeatRowAndSeatNumber(Long roomId, String seatRow, Integer seatNumber);
    boolean existsByRoomIdAndSeatRowAndSeatNumberAndIdNot(Long roomId, String seatRow, Integer seatNumber, Long id);
    int countByRoomId(Long roomId);
    void deleteByRoomId(Long roomId);
}
