package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.UserMessage;
import com.library.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final UserMessageService userMessageService;

    @GetMapping
    public Result<Page<UserMessage>> getMessages(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<UserMessage> page = userMessageService.getMessagePage(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer count = userMessageService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userMessageService.markAsRead(id, userId);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userMessageService.markAllAsRead(userId);
        return Result.success();
    }
}
