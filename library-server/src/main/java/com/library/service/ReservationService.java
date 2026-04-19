package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookReservation;

public interface ReservationService {

    /**
     * 创建预约
     */
    void createReservation(Long bookId, Long userId);

    /**
     * 获取用户的预约列表
     */
    Page<BookReservation> getUserReservations(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 取消预约
     */
    void cancelReservation(Long id, Long userId);

    /**
     * 取书（借阅预约的图书）
     */
    void pickupBook(Long id, Long userId);

    /**
     * 获取图书的预约队列（管理员）
     */
    Page<BookReservation> getBookReservations(Long bookId, Integer pageNum, Integer pageSize);

    /**
     * 通知下一个预约用户（还书时调用）
     */
    void notifyNextReservation(Long bookId);

    /**
     * 处理过期预约（定时任务）
     */
    void processExpiredReservations();
}