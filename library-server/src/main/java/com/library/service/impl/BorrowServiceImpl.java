package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.dto.BorrowRequest;
import com.library.dto.PageRequest;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final com.library.service.ReservationService reservationService;

    @Override
    public Page<BorrowRecord> getBorrowRecords(PageRequest request, Long userId, Integer role) {
        Page<BorrowRecord> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();

        // 所有用户都只能看到自己的借阅记录
        wrapper.eq(BorrowRecord::getUserId, userId);

        if (request.getStatus() != null) {
            wrapper.eq(BorrowRecord::getStatus, request.getStatus());
        } else if (StringUtils.hasText(request.getKeyword())) {
            // Backward compatibility fallback - parse keyword to integer
            try {
                wrapper.eq(BorrowRecord::getStatus, Integer.parseInt(request.getKeyword()));
            } catch (NumberFormatException ignored) {
                // keyword is not a valid status number, skip this filter
            }
        }

        wrapper.orderByDesc(BorrowRecord::getCreateTime);

        Page<BorrowRecord> result = borrowRecordMapper.selectPage(page, wrapper);

        for (BorrowRecord record : result.getRecords()) {
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                record.setBookTitle(book.getTitle());
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void borrowBook(BorrowRequest request, Long userId) {
        Book book = bookMapper.selectForUpdate(request.getBookId());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getStock() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
               .eq(BorrowRecord::getBookId, request.getBookId())
               .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING);
        if (borrowRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("您已借阅此书，尚未归还");
        }

        BorrowRecord record = new BorrowRecord();
        record.setBookId(request.getBookId());
        record.setUserId(userId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(Constants.BORROW_DAYS));
        record.setStatus(Constants.BORROW_STATUS_BORROWING);
        record.setRenewCount(0);

        borrowRecordMapper.insert(record);

        book.setStock(book.getStock() - 1);
        bookMapper.updateById(book);
    }

    @Override
    @Transactional
    public void returnBook(Long id, Long userId) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此借阅记录");
        }
        if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)
            && !record.getStatus().equals(Constants.BORROW_STATUS_OVERDUE)) {
            throw new RuntimeException("该借阅记录无法进行还书操作");
        }

        // 计算滞纳金
        BigDecimal overdueFee = calculateOverdueFee(record.getDueDate());
        record.setOverdueFee(overdueFee);
        record.setReturnDate(LocalDateTime.now());
        record.setStatus(Constants.BORROW_STATUS_RETURNED);
        borrowRecordMapper.updateById(record);

        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            bookMapper.updateById(book);
            // 通知下一个预约者
            reservationService.notifyNextReservation(record.getBookId());
        }
    }

    @Override
    public void renewBook(Long id, Long userId) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此借阅记录");
        }
        if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)) {
            throw new RuntimeException("该图书已归还或已逾期，无法续借");
        }
        // Prevent renew if already overdue
        if (record.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("图书已逾期，请先归还再借");
        }
        // Max 2 renewals per record
        if (record.getRenewCount() != null && record.getRenewCount() >= 2) {
            throw new RuntimeException("该借阅记录已达到最大续借次数");
        }

        int newCount = (record.getRenewCount() == null) ? 1 : record.getRenewCount() + 1;
        record.setRenewCount(newCount);
        record.setDueDate(record.getDueDate().plusDays(Constants.BORROW_DAYS));
        borrowRecordMapper.updateById(record);
    }

    @Override
    public BigDecimal calculateOverdueFee(LocalDateTime dueDate) {
        long overdueDays = LocalDate.now().toEpochDay() - dueDate.toLocalDate().toEpochDay();
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tier1Rate = new BigDecimal("0.20");
        BigDecimal tier2Rate = new BigDecimal("0.50");
        BigDecimal tier3Rate = new BigDecimal("1.00");

        BigDecimal fee;
        if (overdueDays <= 7) {
            fee = BigDecimal.valueOf(overdueDays).multiply(tier1Rate);
        } else if (overdueDays <= 14) {
            fee = BigDecimal.valueOf(7).multiply(tier1Rate)
                    .add(BigDecimal.valueOf(overdueDays - 7).multiply(tier2Rate));
        } else {
            fee = BigDecimal.valueOf(7).multiply(tier1Rate)
                    .add(BigDecimal.valueOf(7).multiply(tier2Rate))
                    .add(BigDecimal.valueOf(overdueDays - 14).multiply(tier3Rate));
        }
        return fee;
    }
}