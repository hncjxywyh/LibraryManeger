package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.UserMessage;

public interface UserMessageService {

    Page<UserMessage> getMessagePage(Long userId, Integer pageNum, Integer pageSize);

    void sendMessage(Long userId, String title, String content, Integer type);

    void markAsRead(Long messageId, Long userId);

    void markAllAsRead(Long userId);

    Integer getUnreadCount(Long userId);
}
