package com.moviebooking.repository;

import com.moviebooking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    boolean existsByMovieId(Long movieId);
    boolean existsByMovieIdAndStartTimeAfter(Long movieId, LocalDateTime now);
    boolean existsByRoomCinemaId(Long cinemaId);
    boolean existsByRoomCinemaIdAndStartTimeAfter(Long cinemaId, LocalDateTime now);
    boolean existsByRoomId(Long roomId);
    boolean existsByRoomIdAndStartTimeAfter(Long roomId, LocalDateTime now);
}
