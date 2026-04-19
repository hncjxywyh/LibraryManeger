package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.entity.Book;
import com.library.entity.BookReservation;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BookReservationMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.ReservationService;
import com.library.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final BookReservationMapper reservationMapper;
    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final UserMessageService userMessageService;

    // 预约状态
    private static final int RESERVATION_STATUS_PENDING = 1;   // 排队中
    private static final int RESERVATION_STATUS_NOTIFIED = 2;  // 已通知
    private static final int RESERVATION_STATUS_CANCELLED = 3; // 已取消
    private static final int RESERVATION_STATUS_EXPIRED = 4;   // 已失效

    // 预约有效期（天）
    private static final int RESERVATION_VALID_DAYS = 7;

    @Override
    @Transactional
    public void createReservation(Long bookId, Long userId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 预约不需要检查库存，预约本来就是针对库存不足的图书

        // 检查用户是否已有该书的有效预约
        LambdaQueryWrapper<BookReservation> existingWrapper = new LambdaQueryWrapper<>();
        existingWrapper.eq(BookReservation::getBookId, bookId)
                .eq(BookReservation::getUserId, userId)
                .eq(BookReservation::getStatus, RESERVATION_STATUS_PENDING);
        if (reservationMapper.selectCount(existingWrapper) > 0) {
            throw new RuntimeException("您已有该书的有效预约");
        }

        // 检查用户是否已借阅该书
        LambdaQueryWrapper<BorrowRecord> borrowWrapper = new LambdaQueryWrapper<>();
        borrowWrapper.eq(BorrowRecord::getBookId, bookId)
                .eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING);
        if (borrowRecordMapper.selectCount(borrowWrapper) > 0) {
            throw new RuntimeException("您已借阅此书");
        }

        // 获取当前最大position
        LambdaQueryWrapper<BookReservation> positionWrapper = new LambdaQueryWrapper<>();
        positionWrapper.eq(BookReservation::getBookId, bookId)
                .eq(BookReservation::getStatus, RESERVATION_STATUS_PENDING)
                .select(BookReservation::getPosition)
                .orderByDesc(BookReservation::getPosition)
                .last("LIMIT 1");
        BookReservation maxPositionReservation = reservationMapper.selectOne(positionWrapper);
        int nextPosition = (maxPositionReservation == null) ? 1 : maxPositionReservation.getPosition() + 1;

        // 创建预约记录
        BookReservation reservation = new BookReservation();
        reservation.setBookId(bookId);
        reservation.setUserId(userId);
        reservation.setStatus(RESERVATION_STATUS_PENDING);
        reservation.setPosition(nextPosition);
        reservation.setCreateTime(LocalDateTime.now());

        reservationMapper.insert(reservation);
    }

    @Override
    public Page<BookReservation> getUserReservations(Long userId, Integer pageNum, Integer pageSize) {
        Page<BookReservation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookReservation::getUserId, userId)
                .orderByDesc(BookReservation::getCreateTime);

        Page<BookReservation> result = reservationMapper.selectPage(page, wrapper);

        // 填充图书信息
        for (BookReservation reservation : result.getRecords()) {
            Book book = bookMapper.selectById(reservation.getBookId());
            if (book != null) {
                reservation.setBookTitle(book.getTitle());
                reservation.setBookAuthor(book.getAuthor());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void cancelReservation(Long id, Long userId) {
        BookReservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约记录");
        }
        if (!reservation.getStatus().equals(RESERVATION_STATUS_PENDING)) {
            throw new RuntimeException("该预约无法取消");
        }

        reservation.setStatus(RESERVATION_STATUS_CANCELLED);
        reservationMapper.updateById(reservation);

        // 更新后续预约的位置
        LambdaQueryWrapper<BookReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookReservation::getBookId, reservation.getBookId())
                .eq(BookReservation::getStatus, RESERVATION_STATUS_PENDING)
                .gt(BookReservation::getPosition, reservation.getPosition());
        List<BookReservation> laterReservations = reservationMapper.selectList(wrapper);
        for (BookReservation later : laterReservations) {
            later.setPosition(later.getPosition() - 1);
            reservationMapper.updateById(later);
        }
    }

    @Override
    @Transactional
    public void pickupBook(Long id, Long userId) {
        BookReservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约记录");
        }
        if (!reservation.getStatus().equals(RESERVATION_STATUS_NOTIFIED)) {
            throw new RuntimeException("该预约状态无法取书");
        }
        if (reservation.getExpireTime() != null && reservation.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("预约已过期");
        }

        Book book = bookMapper.selectById(reservation.getBookId());
        if (book == null || book.getStock() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 检查用户是否已有该书借阅记录
        LambdaQueryWrapper<BorrowRecord> borrowWrapper = new LambdaQueryWrapper<>();
        borrowWrapper.eq(BorrowRecord::getBookId, reservation.getBookId())
                .eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING);
        if (borrowRecordMapper.selectCount(borrowWrapper) > 0) {
            throw new RuntimeException("您已借阅此书");
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBookId(reservation.getBookId());
        record.setUserId(userId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(Constants.BORROW_DAYS));
        record.setStatus(Constants.BORROW_STATUS_BORROWING);
        borrowRecordMapper.insert(record);

        // 更新图书库存
        book.setStock(book.getStock() - 1);
        bookMapper.updateById(book);

        // 删除预约记录
        reservationMapper.deleteById(id);

        // 更新后续预约的位置
        LambdaQueryWrapper<BookReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookReservation::getBookId, reservation.getBookId())
                .eq(BookReservation::getStatus, RESERVATION_STATUS_PENDING)
                .gt(BookReservation::getPosition, reservation.getPosition());
        List<BookReservation> laterReservations = reservationMapper.selectList(wrapper);
        for (BookReservation later : laterReservations) {
            later.setPosition(later.getPosition() - 1);
            reservationMapper.updateById(later);
        }
    }

    @Override
    public Page<BookReservation> getBookReservations(Long bookId, Integer pageNum, Integer pageSize) {
        Page<BookReservation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookReservation::getBookId, bookId)
                .orderByAsc(BookReservation::getPosition);

        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void notifyNextReservation(Long bookId) {
        // 查询该书第一个有效预约（position=1, status=排队中）
        LambdaQueryWrapper<BookReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookReservation::getBookId, bookId)
                .eq(BookReservation::getStatus, RESERVATION_STATUS_PENDING)
                .orderByAsc(BookReservation::getPosition)
                .last("LIMIT 1");
        BookReservation reservation = reservationMapper.selectOne(wrapper);

        if (reservation == null) {
            return;
        }

        // 更新为已通知状态
        LocalDateTime now = LocalDateTime.now();
        reservation.setStatus(RESERVATION_STATUS_NOTIFIED);
        reservation.setNotifyTime(now);
        reservation.setExpireTime(now.plusDays(RESERVATION_VALID_DAYS));
        reservationMapper.updateById(reservation);

        // 发送消息通知用户
        Book book = bookMapper.selectById(bookId);
        String bookTitle = book != null ? book.getTitle() : "图书";
        userMessageService.sendMessage(
                reservation.getUserId(),
                "预约到货通知",
                "您预约的《" + bookTitle + "》已到货，请尽快到图书馆取书。取书截止日期：" + reservation.getExpireTime().toLocalDate(),
                Constants.MSG_TYPE_BOOK_AVAILABLE
        );
    }

    @Override
    @Transactional
    public void processExpiredReservations() {
        // 查询所有已通知但超过7天未取书的预约
        LambdaQueryWrapper<BookReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookReservation::getStatus, RESERVATION_STATUS_NOTIFIED)
                .lt(BookReservation::getExpireTime, LocalDateTime.now());

        List<BookReservation> expiredReservations = reservationMapper.selectList(wrapper);

        for (BookReservation reservation : expiredReservations) {
            reservation.setStatus(RESERVATION_STATUS_EXPIRED);
            reservationMapper.updateById(reservation);

            // 通知后续排队的人（如果还有库存）
            Book book = bookMapper.selectById(reservation.getBookId());
            if (book != null && book.getStock() > 0) {
                notifyNextReservation(reservation.getBookId());
            }
        }
    }
}