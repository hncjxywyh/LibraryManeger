package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.common.Result;
import com.library.dto.PageRequest;
import com.library.entity.User;
import com.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<Page<User>> getUsers(PageRequest request, HttpServletRequest httpRequest) {
        checkAdmin(httpRequest);
        Page<User> page = userService.getUsers(request);
        return Result.success(page);
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user, HttpServletRequest httpRequest) {
        Long currentUserId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (!id.equals(currentUserId) && role != Constants.ROLE_ADMIN) {
            throw new RuntimeException("无权限操作");
        }
        User updated = userService.updateUser(id, user, currentUserId);
        return Result.success(updated);
    }

    @PutMapping("/{id}/password")
    public Result<String> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData, HttpServletRequest httpRequest) {
        Long currentUserId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");
        if (!id.equals(currentUserId) && role != Constants.ROLE_ADMIN) {
            throw new RuntimeException("无权限操作");
        }
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }

    private void checkAdmin(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || !role.equals(Constants.ROLE_ADMIN)) {
            throw new RuntimeException("无权限操作");
        }
    }
}
