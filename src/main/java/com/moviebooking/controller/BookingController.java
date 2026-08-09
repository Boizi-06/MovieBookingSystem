package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.BookingRequest;
import com.moviebooking.dto.BookingResponse;
import com.moviebooking.dto.ShowtimeSeatResponse;
import com.moviebooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/seats/showtime/{showtimeId}")
    public ResponseEntity<ApiResponse<List<ShowtimeSeatResponse>>> getSeatsByShowtimeId(@PathVariable("showtimeId") Long showtimeId) {
        List<ShowtimeSeatResponse> seats = bookingService.getSeatsByShowtimeId(showtimeId);
        ApiResponse<List<ShowtimeSeatResponse>> response = ApiResponse.<List<ShowtimeSeatResponse>>builder()
                .success(true)
                .message("Lấy danh sách sơ đồ ghế của suất chiếu thành công")
                .data(seats)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal String email) {
        BookingResponse bookingResponse = bookingService.createBooking(request, email);
        ApiResponse<BookingResponse> response = ApiResponse.<BookingResponse>builder()
                .success(true)
                .message("Đặt giữ ghế tạm thời thành công (hiệu lực trong 5 phút)")
                .data(bookingResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/bookings/my-history")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingHistory(@AuthenticationPrincipal String email) {
        List<BookingResponse> history = bookingService.getBookingHistory(email);
        ApiResponse<List<BookingResponse>> response = ApiResponse.<List<BookingResponse>>builder()
                .success(true)
                .message("Lấy lịch sử đặt vé thành công")
                .data(history)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/bookings/{id}/payment")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<com.moviebooking.dto.PaymentResponse>> getPaymentDetails(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal String email) {
        com.moviebooking.dto.PaymentResponse paymentResponse = bookingService.getPaymentDetails(id, email);
        ApiResponse<com.moviebooking.dto.PaymentResponse> response = ApiResponse.<com.moviebooking.dto.PaymentResponse>builder()
                .success(true)
                .message("Lấy thông tin thanh toán thành công")
                .data(paymentResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<String>> getBookingStatus(@PathVariable("id") Long id) {
        String status = bookingService.getBookingStatus(id);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Lấy trạng thái đơn hàng thành công")
                .data(status)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/payments/webhook")
    public ResponseEntity<ApiResponse<String>> handlePaymentWebhook(@RequestBody(required = false) String rawJson) {
        try {
            System.out.println("=================================================");
            System.out.println("[PAYMENT WEBHOOK RAW RECEIVED] " + rawJson);
            System.out.println("=================================================");
            boolean processed = bookingService.processPayOSWebhook(rawJson);
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message(processed ? "Xử lý thanh toán thành công" : "Bỏ qua giao dịch không trùng khớp")
                    .data(processed ? "SUCCESS" : "IGNORED")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("[WEBHOOK ERROR] Exception handling webhook: " + e.getMessage());
            e.printStackTrace();
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message("Đã nhận tín hiệu webhook (Lỗi định dạng: " + e.getMessage() + ")")
                    .data("HANDLED_WITH_WARNING")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/payments/payos-webhook")
    public ResponseEntity<ApiResponse<String>> handlePayOSWebhook(@RequestBody(required = false) String rawJson) {
        try {
            System.out.println("=================================================");
            System.out.println("[PAYOS SPECIFIC WEBHOOK RECEIVED] " + rawJson);
            System.out.println("=================================================");
            boolean processed = bookingService.processPayOSWebhook(rawJson);
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message(processed ? "Xử lý PayOS Webhook thành công" : "Bỏ qua giao dịch không trùng khớp")
                    .data(processed ? "SUCCESS" : "IGNORED")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("[PAYOS WEBHOOK ERROR] " + e.getMessage());
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message("Đã nhận PayOS webhook: " + e.getMessage())
                    .data("HANDLED")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/bookings/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal String email) {
        bookingService.cancelBooking(id, email);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Đã hủy giữ ghế thành công. Bạn có thể chọn lại ghế mới.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/payments/mock-success")
    public ResponseEntity<ApiResponse<BookingResponse>> processPaymentMockSuccess(@RequestParam("bookingCode") String bookingCode) {
        BookingResponse bookingResponse = bookingService.processPaymentMockSuccess(bookingCode);
        ApiResponse<BookingResponse> response = ApiResponse.<BookingResponse>builder()
                .success(true)
                .message("Thanh toán thành công và xuất vé điện tử thành công")
                .data(bookingResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/bookings/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<BookingResponse>>> getAllBookingsAdmin(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "movieId", required = false) Long movieId,
            @RequestParam(value = "startDate", required = false) java.time.LocalDate startDate,
            @RequestParam(value = "endDate", required = false) java.time.LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,desc") String sort) {

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        org.springframework.data.domain.Sort.Direction direction = org.springframework.data.domain.Sort.Direction.DESC;
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")) {
            direction = org.springframework.data.domain.Sort.Direction.ASC;
        }

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(direction, sortField));
        org.springframework.data.domain.Page<BookingResponse> bookingPage = bookingService.getAllBookingsAdmin(search, status, movieId, startDate, endDate, pageable);

        ApiResponse<org.springframework.data.domain.Page<BookingResponse>> response = ApiResponse.<org.springframework.data.domain.Page<BookingResponse>>builder()
                .success(true)
                .message("Lấy danh sách tất cả đơn đặt vé thành công")
                .data(bookingPage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
