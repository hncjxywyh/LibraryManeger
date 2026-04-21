package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Constants;
import com.library.entity.Book;
import com.library.entity.BookDonation;
import com.library.mapper.BookDonationMapper;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import com.library.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final BookDonationMapper donationMapper;
    private final BookMapper bookMapper;
    private final BookService bookService;

    @Override
    @Transactional
    public void submitDonation(BookDonation donation, Long userId) {
        donation.setUserId(userId);
        donation.setStatus(1); // 待审核
        donation.setCreateTime(LocalDateTime.now());
        donationMapper.insert(donation);
    }

    @Override
    public Page<BookDonation> getMyDonations(Long userId, Integer pageNum, Integer pageSize) {
        Page<BookDonation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookDonation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookDonation::getUserId, userId)
                .orderByDesc(BookDonation::getCreateTime);
        return donationMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<BookDonation> getAllDonations(BookDonation query, Integer pageNum, Integer pageSize) {
        Page<BookDonation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookDonation> wrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            wrapper.eq(BookDonation::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(BookDonation::getCreateTime);
        return donationMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void reviewDonation(Long id, Integer status, String comment) {
        BookDonation donation = donationMapper.selectById(id);
        if (donation == null) {
            throw new RuntimeException("捐赠记录不存在");
        }
        if (!donation.getStatus().equals(1)) {
            throw new RuntimeException("该捐赠已审核");
        }

        donation.setStatus(status);
        donation.setReviewComment(comment);
        donation.setReviewTime(LocalDateTime.now());
        donationMapper.updateById(donation);

        // 审核通过，自动入库
        if (status == 2) {
            Book book = new Book();
            book.setTitle(donation.getBookTitle());
            book.setAuthor(donation.getBookAuthor());
            book.setPublisher(donation.getBookPublisher());
            book.setStock(donation.getQuantity());
            book.setStatus(0);
            book.setSource("donation");
            book.setDonorId(donation.getUserId());
            bookMapper.insert(book);
        }
    }
}
