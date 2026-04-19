package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_message")
public class UserMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private Integer type; // 1=借阅提醒 2=系统通知 3=预约到货

    private Integer isRead; // 0=未读 1=已读

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
