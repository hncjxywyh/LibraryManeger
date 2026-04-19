package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookDonation;

public interface DonationService {
    void submitDonation(BookDonation donation, Long userId);
    Page<BookDonation> getMyDonations(Long userId, Integer pageNum, Integer pageSize);
    Page<BookDonation> getAllDonations(BookDonation query, Integer pageNum, Integer pageSize);
    void reviewDonation(Long id, Integer status, String comment);
}
