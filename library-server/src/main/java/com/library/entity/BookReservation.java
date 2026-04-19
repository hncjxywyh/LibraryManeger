package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_reservation")
public class BookReservation {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("book_id")
    private Long bookId;

    @TableField("user_id")
    private Long userId;

    private Integer status; // 1=排队中 2=已通知 3=已取消 4=已失效

    private Integer position; // 队列位置

    @TableField("notify_time")
    private LocalDateTime notifyTime; // 通知时间

    @TableField("expire_time")
    private LocalDateTime expireTime; // 预约过期时间（notifyTime+7天）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String bookTitle;

    @TableField(exist = false)
    private String bookAuthor;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userRealName;
}