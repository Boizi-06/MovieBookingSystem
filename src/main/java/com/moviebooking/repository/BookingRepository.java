package com.moviebooking.repository;

import com.moviebooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.showtime.id = :showtimeId AND " +
           "(b.status IN ('PAID', 'CONFIRMED', 'COMPLETED') OR " +
           "(b.status = 'PENDING_PAYMENT' AND b.createdAt > :threshold))")
    List<Booking> findActiveBookingsByShowtime(
            @Param("showtimeId") Long showtimeId,
            @Param("threshold") LocalDateTime threshold);

    List<Booking> findByStatusAndCreatedAtBefore(String status, LocalDateTime threshold);
    java.util.Optional<Booking> findByBookingCode(String bookingCode);

    @Query("SELECT b FROM Booking b WHERE REPLACE(REPLACE(LOWER(b.bookingCode), '-', ''), ' ', '') = REPLACE(REPLACE(LOWER(:code), '-', ''), ' ', '')")
    java.util.Optional<Booking> findByNormalizedBookingCode(@Param("code") String code);
    List<Booking> findByUserEmailOrderByCreatedAtDesc(String email);
    boolean existsByShowtimeId(Long showtimeId);

    @Query("SELECT DISTINCT b FROM Booking b " +
           "JOIN FETCH b.seats " +
           "JOIN FETCH b.showtime s " +
           "JOIN FETCH s.movie " +
           "WHERE b.status IN ('PAID', 'CONFIRMED', 'COMPLETED') " +
           "AND b.createdAt >= :start AND b.createdAt < :end")
    List<Booking> findPaidBookingsWithDetails(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query(value = "SELECT b FROM Booking b " +
           "LEFT JOIN b.user u " +
           "LEFT JOIN b.showtime s " +
           "LEFT JOIN s.movie m " +
           "WHERE (:status IS NULL OR b.status = :status) " +
           "AND (:movieId IS NULL OR m.id = :movieId) " +
           "AND (:startDate IS NULL OR b.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR b.createdAt <= :endDate) " +
           "AND (:search IS NULL OR LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT COUNT(b) FROM Booking b " +
           "LEFT JOIN b.user u " +
           "LEFT JOIN b.showtime s " +
           "LEFT JOIN s.movie m " +
           "WHERE (:status IS NULL OR b.status = :status) " +
           "AND (:movieId IS NULL OR m.id = :movieId) " +
           "AND (:startDate IS NULL OR b.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR b.createdAt <= :endDate) " +
           "AND (:search IS NULL OR LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :search, '%')))")
    org.springframework.data.domain.Page<Booking> findAllAdminBookings(
            @Param("search") String search,
            @Param("status") String status,
            @Param("movieId") Long movieId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            org.springframework.data.domain.Pageable pageable);
}
