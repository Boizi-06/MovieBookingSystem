package com.moviebooking.service.impl;

import com.moviebooking.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendResetPasswordEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[MovieBooking] Mã xác thực khôi phục mật khẩu");
            message.setText("Xin chào,\n\nBạn nhận được email này vì đã gửi yêu cầu khôi phục mật khẩu cho tài khoản tại MovieBooking.\n\n" +
                    "MÃ XÁC THỰC KHÔI PHỤC MẬT KHẨU CỦA BẠN LÀ:\n\n" +
                    "   " + token + "\n\n" +
                    "Vui lòng sao chép mã xác thực trên và nhập vào trang Đặt lại mật khẩu trên website để hoàn tất.\n\n" +
                    "Mã này có hiệu lực trong vòng 15 phút.\n\nTrân trọng,\nĐội ngũ MovieBooking.");
            
            mailSender.send(message);
            System.out.println("[EMAIL SUCCESS] Gửi email mã khôi phục mật khẩu tới: " + toEmail + " | Token: " + token);
        } catch (Exception e) {
            System.err.println("\n==========================================================================");
            System.err.println("[EMAIL ERROR] Không thể gửi email khôi phục mật khẩu thực tế!");
            System.err.println("Gửi tới email: " + toEmail);
            System.err.println("Mã xác thực khôi phục (Token): " + token);
            System.err.println("Lỗi chi tiết: " + e.getMessage());
            System.err.println("==========================================================================\n");
        }
    }

    @Override
    public void sendVerificationEmail(String toEmail, String verifyLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[MovieBooking] Xác thực kích hoạt tài khoản");
            message.setText("Xin chào,\n\nCảm ơn bạn đã đăng ký tài khoản tại MovieBooking.\n\n" +
                    "Vui lòng truy cập đường dẫn sau để kích hoạt tài khoản của bạn:\n" + verifyLink + "\n\n" +
                    "Kích hoạt tài khoản giúp bạn có thể đặt vé xem phim trực tuyến.\n\nTrân trọng,\nĐội ngũ MovieBooking.");
            
            mailSender.send(message);
            System.out.println("[EMAIL SUCCESS] Gửi email kích hoạt tài khoản thực tế tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("\n==========================================================================");
            System.err.println("[EMAIL ERROR] Không thể gửi email kích hoạt tài khoản thực tế!");
            System.err.println("Gửi tới email: " + toEmail);
            System.err.println("Lỗi chi tiết: " + e.getMessage());
            System.err.println("Đường dẫn kích hoạt tài khoản: " + verifyLink);
            System.err.println("==========================================================================\n");
        }
    }

    @Override
    public void sendTicketEmail(com.moviebooking.entity.Booking booking) {
        if (booking == null || booking.getUser() == null || booking.getUser().getEmail() == null) return;
        String toEmail = booking.getUser().getEmail();

        try {
            jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[MovieBooking] XÁC NHẬN ĐẶT VÉ THÀNH CÔNG - " + booking.getBookingCode());

            String seatsStr = booking.getSeats().stream()
                    .map(com.moviebooking.entity.Seat::getSeatCode)
                    .sorted()
                    .collect(java.util.stream.Collectors.joining(", "));

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
            String startTimeStr = booking.getShowtime().getStartTime().format(formatter);

            String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + booking.getBookingCode();
            String ticketWebUrl = "http://localhost:5173/booking/success/" + booking.getBookingCode();

            String htmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #0f172a; color: #f8fafc; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background: #1e293b; border-radius: 12px; padding: 24px; border: 1px solid #334155; box-shadow: 0 10px 25px rgba(0,0,0,0.5);'>" +
                    "<div style='text-align: center; border-bottom: 2px dashed #475569; padding-bottom: 16px; margin-bottom: 20px;'>" +
                    "<h1 style='color: #eab308; margin: 0; font-size: 24px; letter-spacing: 1px;'>🎟️ VÉ XEM PHIM ĐIỆN TỬ</h1>" +
                    "<p style='color: #94a3b8; margin: 5px 0 0 0; font-size: 14px;'>Movie Booking System</p>" +
                    "</div>" +

                    "<div style='margin-bottom: 20px; text-align: center;'>" +
                    "<img src='" + qrUrl + "' alt='QR Code' style='border: 4px solid #ffffff; border-radius: 8px; width: 140px; height: 140px;'/>" +
                    "<p style='margin-top: 8px; font-family: monospace; font-size: 18px; font-weight: bold; color: #38bdf8;'>" + booking.getBookingCode() + "</p>" +
                    "</div>" +

                    "<table style='width: 100%; font-size: 15px; border-collapse: collapse; margin-bottom: 20px;'>" +
                    "<tr style='border-bottom: 1px solid #334155;'><td style='padding: 10px 0; color: #94a3b8;'>Phim:</td><td style='padding: 10px 0; text-align: right; font-weight: bold; color: #f8fafc;'>" + booking.getShowtime().getMovie().getTitle() + "</td></tr>" +
                    "<tr style='border-bottom: 1px solid #334155;'><td style='padding: 10px 0; color: #94a3b8;'>Rạp chiếu:</td><td style='padding: 10px 0; text-align: right; font-weight: bold; color: #f8fafc;'>" + booking.getShowtime().getRoom().getCinema().getName() + "</td></tr>" +
                    "<tr style='border-bottom: 1px solid #334155;'><td style='padding: 10px 0; color: #94a3b8;'>Phòng chiếu:</td><td style='padding: 10px 0; text-align: right; font-weight: bold; color: #f8fafc;'>" + booking.getShowtime().getRoom().getName() + "</td></tr>" +
                    "<tr style='border-bottom: 1px solid #334155;'><td style='padding: 10px 0; color: #94a3b8;'>Suất chiếu:</td><td style='padding: 10px 0; text-align: right; font-weight: bold; color: #f8fafc;'>" + startTimeStr + "</td></tr>" +
                    "<tr style='border-bottom: 1px solid #334155;'><td style='padding: 10px 0; color: #94a3b8;'>Ghế ngồi:</td><td style='padding: 10px 0; text-align: right; font-weight: bold; color: #eab308;'>" + seatsStr + "</td></tr>" +
                    "<tr><td style='padding: 10px 0; color: #94a3b8;'>Tổng tiền:</td><td style='padding: 10px 0; text-align: right; font-weight: bold; font-size: 18px; color: #22c55e;'>" + String.format("%,d VNĐ", booking.getTotalPrice().longValue()) + "</td></tr>" +
                    "</table>" +

                    "<div style='text-align: center; margin-top: 25px;'>" +
                    "<a href='" + ticketWebUrl + "' style='background: linear-gradient(135deg, #eab308, #ca8a04); color: #000000; font-weight: bold; text-decoration: none; padding: 12px 24px; border-radius: 8px; display: inline-block;'>XEM VÉ CHI TIẾT TẠI WEBSITE</a>" +
                    "</div>" +

                    "<div style='margin-top: 25px; border-top: 1px solid #334155; padding-top: 15px; text-align: center; font-size: 12px; color: #64748b;'>" +
                    "<p>Vui lòng xuất trình mã QR này tại quầy vé trước khi vào phòng chiếu.</p>" +
                    "<p>Cảm ơn bạn đã lựa chọn dịch vụ của Movie Booking!</p>" +
                    "</div>" +
                    "</div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            System.out.println("[EMAIL SUCCESS] Đã gửi Email Cuống Vé HTML thành công tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] Không thể gửi Email Cuống Vé: " + e.getMessage());
        }
    }
}
