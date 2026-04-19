package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("borrow_remind_log")
public class BorrowRemindLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long borrowId;

    private Long userId;

    private Integer remindType; // 1=即将到期 2=已逾期

    private Integer remindDays; // 提前几天提醒/逾期几天

    private Integer remindStatus; // 0=待发送 1=已发送 2=发送失败

    private LocalDateTime sendTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
