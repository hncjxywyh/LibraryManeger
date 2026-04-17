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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;

    @Override
    public Page<BorrowRecord> getBorrowRecords(PageRequest request, Long userId, Integer role) {
        Page<BorrowRecord> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();

        if (role != null && role.equals(Constants.ROLE_USER)) {
            wrapper.eq(BorrowRecord::getUserId, userId);
        }

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.eq(BorrowRecord::getStatus, request.getKeyword());
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
        Book book = bookMapper.selectById(request.getBookId());
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

        borrowRecordMapper.insert(record);

        book.setStock(book.getStock() - 1);
        bookMapper.updateById(book);
    }

    @Override
    @Transactional
    public void returnBook(Long id) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)) {
            throw new RuntimeException("该图书已归还");
        }

        record.setReturnDate(LocalDateTime.now());
        record.setStatus(Constants.BORROW_STATUS_RETURNED);
        borrowRecordMapper.updateById(record);

        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            bookMapper.updateById(book);
        }
    }

    @Override
    public void renewBook(Long id) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        if (!record.getStatus().equals(Constants.BORROW_STATUS_BORROWING)) {
            throw new RuntimeException("该图书已归还，无法续借");
        }

        record.setDueDate(record.getDueDate().plusDays(Constants.BORROW_DAYS));
        borrowRecordMapper.updateById(record);
    }
}