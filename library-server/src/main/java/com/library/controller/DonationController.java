package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.dto.PageRequest;
import com.library.entity.BookDonation;
import com.library.service.DonationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public Result<String> submitDonation(@RequestBody BookDonation donation, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        donationService.submitDonation(donation, userId);
        return Result.success("捐赠提交成功");
    }

    @GetMapping
    public Result<Page<BookDonation>> getMyDonations(PageRequest pageRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<BookDonation> page = donationService.getMyDonations(
                userId,
                pageRequest.getPageNum(),
                pageRequest.getPageSize()
        );
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<Page<BookDonation>> getAllDonations(
            BookDonation query,
            PageRequest pageRequest,
            HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role != 1) {
            return Result.error("无权限访问");
        }
        Page<BookDonation> page = donationService.getAllDonations(
                query,
                pageRequest.getPageNum(),
                pageRequest.getPageSize()
        );
        return Result.success(page);
    }

    @PutMapping("/{id}")
    public Result<String> reviewDonation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role != 1) {
            return Result.error("无权限访问");
        }
        Integer status = (Integer) body.get("status");
        String comment = (String) body.get("comment");
        donationService.reviewDonation(id, status, comment);
        return Result.success("审核完成");
    }
}
