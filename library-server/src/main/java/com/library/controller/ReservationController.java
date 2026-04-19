package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.PageRequest;
import com.library.entity.BookReservation;
import com.library.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{bookId}")
    public Result<String> createReservation(@PathVariable Long bookId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        reservationService.createReservation(bookId, userId);
        return Result.success("预约成功");
    }

    @GetMapping
    public Result<Page<BookReservation>> getMyReservations(PageRequest pageRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<BookReservation> page = reservationService.getUserReservations(
                userId,
                pageRequest.getPageNum(),
                pageRequest.getPageSize()
        );
        return Result.success(page);
    }

    @DeleteMapping("/{id}")
    public Result<String> cancelReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        reservationService.cancelReservation(id, userId);
        return Result.success("取消预约成功");
    }

    @PostMapping("/{id}/pickup")
    public Result<String> pickupReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        reservationService.pickupBook(id, userId);
        return Result.success("取书成功");
    }

    @GetMapping("/book/{bookId}")
    public Result<Page<BookReservation>> getBookReservations(
            @PathVariable Long bookId,
            PageRequest pageRequest,
            HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role != 1) {
            return Result.error("无权限访问");
        }
        Page<BookReservation> page = reservationService.getBookReservations(
                bookId,
                pageRequest.getPageNum(),
                pageRequest.getPageSize()
        );
        return Result.success(page);
    }
}
