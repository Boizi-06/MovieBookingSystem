package com.moviebooking.service;

import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.BookingResponse;
import com.moviebooking.dto.ShowtimeSeatResponse;

import java.util.List;

public interface BookingService {
    List<ShowtimeSeatResponse> getSeatsByShowtimeId(Long showtimeId);
    BookingResponse createBooking(BookingRequest request, String userEmail);
    void cancelExpiredBookings();
    List<BookingResponse> getBookingHistory(String userEmail);
    com.moviebooking.dto.PaymentResponse getPaymentDetails(Long bookingId, String userEmail);
    void cancelBooking(Long bookingId, String userEmail);
    boolean processSepayWebhook(com.moviebooking.dto.SepayWebhookPayload payload);
    boolean processSepayWebhookRaw(String rawJson);
    boolean processPayOSWebhook(String rawJson);
    String getBookingStatus(Long bookingId);
    BookingResponse processPaymentMockSuccess(String bookingCode);
    org.springframework.data.domain.Page<BookingResponse> getAllBookingsAdmin(
            String search,
            String status,
            Long movieId,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            org.springframework.data.domain.Pageable pageable);
}
