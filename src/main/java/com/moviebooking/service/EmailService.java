package com.moviebooking.service;

public interface EmailService {
    void sendResetPasswordEmail(String toEmail, String resetLink);
}
