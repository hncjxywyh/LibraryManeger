package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.BorrowRequest;
import com.library.dto.PageRequest;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @GetMapping
    public Result<Page<BorrowRecord>> getBorrowRecords(PageRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer role = (Integer) httpRequest.getAttribute("role");
        Page<BorrowRecord> page = borrowService.getBorrowRecords(request, userId, role);
        return Result.success(page);
    }

    @PostMapping
    public Result<Void> borrowBook(@RequestBody BorrowRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        borrowService.borrowBook(request, userId);
        return Result.success("借书成功");
    }

    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return Result.success("还书成功");
    }

    @PutMapping("/{id}/renew")
    public Result<Void> renewBook(@PathVariable Long id) {
        borrowService.renewBook(id);
        return Result.success("续借成功");
    }
}