package com.moviebooking.service.impl;

import com.moviebooking.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class MockEmailService implements EmailService {

    @Override
    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        System.out.println("\n==========================================================================");
        System.out.println("[EMAIL MOCK] GỬI EMAIL KHÔI PHỤC MẬT KHẨU THÀNH CÔNG!");
        System.out.println("Gửi tới email: " + toEmail);
        System.out.println("Vui lòng truy cập đường dẫn sau để đặt lại mật khẩu:");
        System.out.println(resetLink);
        System.out.println("==========================================================================\n");
    }
}
