package com.moviebooking.config;

import com.moviebooking.entity.User;
import com.moviebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Khởi tạo tài khoản Admin mẫu nếu chưa tồn tại
        if (!userRepository.existsByEmail("admin@moviebooking.com")) {
            User admin = User.builder()
                    .fullname("System Administrator")
                    .email("admin@moviebooking.com")
                    .password(passwordEncoder.encode("AdminPassword123"))
                    .role("ADMIN")
                    .status("ACTIVE")
                    .build();
            userRepository.save(admin);
            System.out.println("\n==========================================================================");
            System.out.println("[DATA SEEDER] TẠO TÀI KHOẢN ADMIN MẪU THÀNH CÔNG!");
            System.out.println("Email: admin@moviebooking.com");
            System.out.println("Mật khẩu: AdminPassword123");
            System.out.println("==========================================================================\n");
        }
    }
}
