package com.moviebooking.service;

import com.moviebooking.dto.*;
import com.moviebooking.entity.RefreshToken;
import com.moviebooking.entity.PasswordResetToken;
import com.moviebooking.entity.EmailVerificationToken;
import com.moviebooking.entity.User;
import com.moviebooking.repository.RefreshTokenRepository;
import com.moviebooking.repository.PasswordResetTokenRepository;
import com.moviebooking.repository.EmailVerificationTokenRepository;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // Thời hạn của Refresh Token: 7 ngày (604.800.000 ms)
    private final long refreshTokenExpirationMs = 604800000;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       EmailVerificationTokenRepository emailVerificationTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public User register(RegisterRequest request) {
        // 1. Kiểm tra mật khẩu xác nhận có khớp không (BR-AUTH-05)
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        // 2. Kiểm tra email đã tồn tại chưa (BR-AUTH-01)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }

        // 3. Mã hóa mật khẩu (BR-AUTH-02)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. Tạo người dùng mới với vai trò mặc định CUSTOMER và trạng thái INACTIVE (Chờ xác thực email)
        User user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .phone(request.getPhone() != null && request.getPhone().trim().isEmpty() ? null : request.getPhone())
                .password(encodedPassword)
                .role("CUSTOMER")
                .status("INACTIVE")
                .build();
        User savedUser = userRepository.save(user);

        // 5. Sinh và lưu EmailVerificationToken kích hoạt tài khoản (Thời hạn 24 giờ)
        String verifyToken = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(savedUser)
                .token(verifyToken)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        emailVerificationTokenRepository.save(verificationToken);

        // 6. Xây dựng đường dẫn kích hoạt trỏ tới Frontend
        String verifyLink = frontendUrl + "/verify-email?token=" + verifyToken;

        // 7. Gửi email kích hoạt tài khoản
        emailService.sendVerificationEmail(savedUser.getEmail(), verifyLink);

        return savedUser;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. Tìm tài khoản theo email. Nếu không thấy -> Trả lỗi chung bảo mật (BR-AUTH-08)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không chính xác"));

        // 2. So sánh mật khẩu. Nếu không khớp -> Trả lỗi chung bảo mật (BR-AUTH-08)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không chính xác");
        }

        // 3. Kiểm tra trạng thái tài khoản (BR-AUTH-07)
        if (!"ACTIVE".equals(user.getStatus())) {
            if ("INACTIVE".equals(user.getStatus())) {
                throw new IllegalArgumentException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn để kích hoạt.");
            } else if ("LOCKED".equals(user.getStatus())) {
                throw new IllegalArgumentException("Tài khoản của bạn đã bị khóa");
            } else {
                throw new IllegalArgumentException("Tài khoản của bạn đã bị ngừng hoạt động");
            }
        }

        // 4. Xóa các Refresh Token cũ của người dùng này nếu có
        refreshTokenRepository.deleteByUser(user);

        // 5. Tạo mới Refresh Token dạng UUID và lưu vào Database
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();
        refreshTokenRepository.save(refreshToken);

        // 6. Sinh Access Token (JWT)
        String accessToken = jwtTokenProvider.generateToken(user);

        // 7. Trả về thông tin đăng nhập thành công chứa cả 2 tokens
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(user)
                .build();
    }

    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        // 1. Tìm Refresh Token trong Database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token không tồn tại hoặc đã hết hiệu lực"));

        // 2. Kiểm tra Refresh Token đã hết hạn chưa
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh Token đã hết hạn. Vui lòng đăng nhập lại");
        }

        // 3. Sử dụng cơ chế Xoay vòng Refresh Token (Rotation): Lấy User và sinh Access Token mới trước
        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user);

        // 4. Cập nhật trực tiếp token mới vào bản ghi hiện tại để tránh lỗi trùng khoá ngoại user_id
        String newRefreshTokenString = UUID.randomUUID().toString();
        refreshToken.setToken(newRefreshTokenString);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshTokenRepository.save(refreshToken);

        // 5. Trả về cặp Token mới cho client
        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenString)
                .build();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        // Quy tắc bảo mật (BR-AUTH-16): Không báo lỗi nếu email không tồn tại.
        if (user != null) {
            // Xóa token khôi phục cũ của user này nếu có
            passwordResetTokenRepository.deleteByUser(user);

            // Tạo mới PasswordResetToken có thời hạn 15 phút (BR-AUTH-13)
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .user(user)
                    .token(token)
                    .expiryDate(Instant.now().plus(15, ChronoUnit.MINUTES))
                    .used(false)
                    .build();
            passwordResetTokenRepository.save(resetToken);

            // Gửi email chứa mã xác thực (token) tới khách hàng
            emailService.sendResetPasswordEmail(user.getEmail(), token);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Kiểm tra hai mật khẩu mới nhập vào có trùng khớp không (BR-AUTH-18)
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        // 2. Tìm Token trong Database
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Đường dẫn khôi phục mật khẩu không hợp lệ hoặc đã hết hạn"));

        // 3. Kiểm tra tính hợp lệ của Token (đã sử dụng chưa hoặc đã quá hạn chưa) (BR-AUTH-12, BR-AUTH-13)
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Đường dẫn khôi phục mật khẩu không hợp lệ hoặc đã hết hạn");
        }

        // 4. Mã hóa và cập nhật mật khẩu mới cho người dùng (BR-AUTH-14)
        User user = resetToken.getUser();
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // 5. Đánh dấu token đã được sử dụng (BR-AUTH-15)
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public void verifyEmail(String token) {
        // 1. Tìm Token kích hoạt trong Database
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Mã xác thực tài khoản không hợp lệ hoặc đã hết hạn."));

        // 2. Kiểm tra Token đã hết hạn chưa
        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("Mã xác thực tài khoản đã hết hạn.");
        }

        // 3. Kích hoạt tài khoản người dùng
        User user = verificationToken.getUser();
        user.setStatus("ACTIVE");
        userRepository.save(user);

        // 4. Xoá Token xác thực này đi
        emailVerificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        // 1. Tìm người dùng theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));

        // 2. Kiểm tra mật khẩu hiện tại (BR-AUTH-17)
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }

        // 3. Kiểm tra mật khẩu xác nhận khớp với mật khẩu mới (BR-AUTH-18)
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        // 4. Kiểm tra mật khẩu mới có trùng mật khẩu cũ không (BR-AUTH-19)
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng hoàn toàn với mật khẩu hiện tại");
        }

        // 5. Mã hóa mật khẩu mới và lưu vào DB (BR-AUTH-21)
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}

