package com.moviebooking.service;

import com.moviebooking.dto.UpdateProfileRequest;
import com.moviebooking.dto.UpdateUserStatusRequest;
import com.moviebooking.dto.UserResponse;
import com.moviebooking.entity.User;
import com.moviebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin người dùng"));
        return UserResponse.fromUser(user);
    }

    @Transactional
    public UserResponse updateUserProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin người dùng"));

        user.setFullname(request.getFullname());
        user.setPhone(request.getPhone() != null && request.getPhone().trim().isEmpty() ? null : request.getPhone());
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String keyword, String role, String status, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String filterRole = (role != null && !role.trim().isEmpty()) ? role.trim() : null;
        String filterStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;

        Page<User> users = userRepository.searchUsers(searchKeyword, filterRole, filterStatus, pageable);
        return users.map(UserResponse::fromUser);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, String adminEmail, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));

        // Ràng buộc nghiệp vụ: Admin không được tự khóa chính mình
        if (user.getEmail().equalsIgnoreCase(adminEmail)) {
            throw new IllegalArgumentException("Bạn không thể tự khóa tài khoản của chính mình");
        }

        user.setStatus(request.getStatus());
        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }
}
