package com.library.controller;

import com.library.common.Result;
import com.library.service.BorrowRemindService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user-stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final BorrowRemindService borrowRemindService;

    @GetMapping
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> stats = borrowRemindService.getUserBorrowStats(userId);
        return Result.success(stats);
    }
}
