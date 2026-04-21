package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer status;    // 1-借阅中 2-已归还 3-逾期

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String bookTitle;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userRealName;

    private Integer remindSent; // 是否已发送到期提醒 0=未发 1=已发

    private Integer overdueRemindCount; // 逾期提醒次数

    private BigDecimal overdueFee; // 滞纳金

    private Integer renewCount; // 续借次数
}