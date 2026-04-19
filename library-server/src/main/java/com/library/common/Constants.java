package com.library.common;

public class Constants {
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_USER = 2;

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_DISABLED = 0;

    public static final int BORROW_STATUS_BORROWING = 1;
    public static final int BORROW_STATUS_RETURNED = 2;
    public static final int BORROW_STATUS_OVERDUE = 3;

    public static final int BOOK_STATUS_ON = 1;
    public static final int BOOK_STATUS_OFF = 0;

    public static final int BORROW_DAYS = 30;

    // 消息类型
    public static final int MSG_TYPE_BORROW_REMIND = 1;
    public static final int MSG_TYPE_SYSTEM = 2;
    public static final int MSG_TYPE_BOOK_AVAILABLE = 3;

    // 提醒类型
    public static final int REMIND_TYPE_COMING_DUE = 1;
    public static final int REMIND_TYPE_OVERDUE = 2;

    // 提醒状态
    public static final int REMIND_STATUS_PENDING = 0;
    public static final int REMIND_STATUS_SENT = 1;
    public static final int REMIND_STATUS_FAILED = 2;

    // 提醒配置
    public static final int REMIND_BEFORE_DAYS = 3; // 提前3天提醒
    public static final int OVERDUE_RESEND_DAYS = 1; // 逾期后每天重复提醒
}