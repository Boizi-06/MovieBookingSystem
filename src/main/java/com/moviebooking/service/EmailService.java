package com.moviebooking.service;

public interface EmailService {
    void sendResetPasswordEmail(String toEmail, String token);
    void sendVerificationEmail(String toEmail, String verifyLink);
    void sendTicketEmail(com.moviebooking.entity.Booking booking);
}
