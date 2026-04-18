package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.LoginRequest;
import com.library.dto.PageRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;

public interface UserService {
    User register(RegisterRequest request);
    String login(LoginRequest request);
    User getCurrentUser(Long userId);
    User getUserById(Long id);
    User updateUser(Long id, User user, Long adminId);
    void changePassword(Long id, String oldPassword, String newPassword);
    Page<User> getUsers(PageRequest request);
}