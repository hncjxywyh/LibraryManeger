package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.BorrowRequest;
import com.library.dto.PageRequest;
import com.library.entity.BorrowRecord;

public interface BorrowService {
    Page<BorrowRecord> getBorrowRecords(PageRequest request, Long userId, Integer role);
    void borrowBook(BorrowRequest request, Long userId);
    void returnBook(Long id, Long userId);
    void renewBook(Long id, Long userId);
}