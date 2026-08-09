package com.moviebooking.repository;

import com.moviebooking.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    boolean existsByMovieId(Long movieId);
    boolean existsByMovieIdAndStartTimeAfter(Long movieId, LocalDateTime now);
    boolean existsByRoomCinemaId(Long cinemaId);
    boolean existsByRoomCinemaIdAndStartTimeAfter(Long cinemaId, LocalDateTime now);
    boolean existsByRoomId(Long roomId);
    boolean existsByRoomIdAndStartTimeAfter(Long roomId, LocalDateTime now);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.room.id = :roomId AND " +
           "((:id IS NULL) OR (s.id != :id)) AND " +
           "s.status != 'CANCELLED' AND " +
           "(:startTime < s.endTime AND :endTime > s.startTime)")
    boolean existsOverlappingShowtime(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("id") Long id);

    @Query("SELECT s FROM Showtime s WHERE " +
           "(:movieId IS NULL OR s.movie.id = :movieId) AND " +
           "(:cinemaId IS NULL OR s.room.cinema.id = :cinemaId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:dateStart IS NULL OR s.startTime >= :dateStart) AND " +
           "(:dateEnd IS NULL OR s.startTime < :dateEnd) " +
           "ORDER BY s.startTime ASC")
    List<Showtime> searchShowtimes(
            @Param("movieId") Long movieId,
            @Param("cinemaId") Long cinemaId,
            @Param("status") String status,
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateEnd") LocalDateTime dateEnd);
}
