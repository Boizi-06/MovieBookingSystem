package com.moviebooking.repository;

import com.moviebooking.entity.BookingCombo;
import com.moviebooking.entity.BookingComboId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingComboRepository extends JpaRepository<BookingCombo, BookingComboId> {
    List<BookingCombo> findByBookingId(Long bookingId);
}
