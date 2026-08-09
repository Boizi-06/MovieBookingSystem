package com.moviebooking.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String bookingCode;
    private Long showtimeId;
    private BigDecimal amount;
    private String bankId;        // ví dụ: MB, VCB...
    private String accountNo;     // Số tài khoản
    private String accountName;   // Tên chủ tài khoản
    private String transferMemo;  // Nội dung chuyển khoản bắt buộc (bookingCode)
    private String qrCodeUrl;     // URL ảnh VietQR
    private String checkoutUrl;   // Link thanh toán mở trực tiếp trang PayOS
    private Long orderCode;       // Mã đơn hàng PayOS (định dạng số)
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime expiresAt;
    private Long remainingSeconds;
    private java.util.List<BookingResponse.BookingComboResponse> comboItems;
}
