package com.library.service;

import java.util.Map;

public interface BorrowRemindService {

    void processRemindTask();

    Map<String, Object> getUserBorrowStats(Long userId);
}
