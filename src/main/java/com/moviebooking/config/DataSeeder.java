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
    private final com.moviebooking.repository.GenreRepository genreRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataSeeder(UserRepository userRepository, com.moviebooking.repository.GenreRepository genreRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
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

        // Khởi tạo thể loại phim mẫu nếu chưa tồn tại
        if (genreRepository.count() == 0) {
            genreRepository.save(com.moviebooking.entity.Genre.builder().name("Hành động").description("Phim hành động gay cấn, kịch tính").build());
            genreRepository.save(com.moviebooking.entity.Genre.builder().name("Tình cảm").description("Phim lãng mạn, tình cảm gia đình").build());
            genreRepository.save(com.moviebooking.entity.Genre.builder().name("Hài hước").description("Phim hài hước mang lại tiếng cười").build());
            genreRepository.save(com.moviebooking.entity.Genre.builder().name("Kinh dị").description("Phim rùng rợn, kinh dị").build());
            genreRepository.save(com.moviebooking.entity.Genre.builder().name("Hoạt hình").description("Phim hoạt hình dành cho mọi lứa tuổi").build());
            genreRepository.save(com.moviebooking.entity.Genre.builder().name("Viễn tưởng").description("Phim khoa học viễn tưởng").build());
            System.out.println("[DATA SEEDER] Khởi tạo các thể loại phim mẫu thành công!");
        }
    }
}
