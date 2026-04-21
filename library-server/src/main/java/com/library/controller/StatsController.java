package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> stats = new HashMap<>();

        // 图书总数
        long totalBooks = bookMapper.selectCount(null);
        stats.put("totalBooks", totalBooks);

        // 读者数量（非管理员用户，role != 1）
        LambdaQueryWrapper<User> readerWrapper = new LambdaQueryWrapper<>();
        readerWrapper.ne(User::getRole, 1);
        long readerCount = userMapper.selectCount(readerWrapper);
        stats.put("readerCount", readerCount);

        return Result.success(stats);
    }
}
