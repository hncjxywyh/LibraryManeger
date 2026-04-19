package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.UserMessage;
import com.library.mapper.UserMessageMapper;
import com.library.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMessageServiceImpl implements UserMessageService {

    private final UserMessageMapper userMessageMapper;

    @Override
    public Page<UserMessage> getMessagePage(Long userId, Integer pageNum, Integer pageSize) {
        Page<UserMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .orderByDesc(UserMessage::getCreateTime);
        return userMessageMapper.selectPage(page, wrapper);
    }

    @Override
    public void sendMessage(Long userId, String title, String content, Integer type) {
        UserMessage message = new UserMessage();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setIsRead(0);
        userMessageMapper.insert(message);
    }

    @Override
    public void markAsRead(Long messageId, Long userId) {
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getId, messageId)
               .eq(UserMessage::getUserId, userId);
        UserMessage message = userMessageMapper.selectOne(wrapper);
        if (message != null) {
            message.setIsRead(1);
            userMessageMapper.updateById(message);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .eq(UserMessage::getIsRead, 0);
        UserMessage update = new UserMessage();
        update.setIsRead(1);
        userMessageMapper.update(update, wrapper);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .eq(UserMessage::getIsRead, 0);
        return Math.toIntExact(userMessageMapper.selectCount(wrapper));
    }
}
