package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.library.common.Constants;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.BorrowRemindLog;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.BorrowRemindLogMapper;
import com.library.mapper.BookMapper;
import com.library.service.BorrowRemindService;
import com.library.service.UserMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BorrowRemindServiceImpl implements BorrowRemindService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BorrowRemindLogMapper borrowRemindLogMapper;
    private final BookMapper bookMapper;
    private final UserMessageService userMessageService;

    @Override
    @Transactional
    public void processRemindTask() {
        LocalDate today = LocalDate.now();

        // 1. 处理即将到期的借阅（提前3天）
        LocalDate dueDateThreshold = today.plusDays(Constants.REMIND_BEFORE_DAYS);
        LambdaQueryWrapper<BorrowRecord> dueWrapper = new LambdaQueryWrapper<>();
        dueWrapper.eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING)
                  .eq(BorrowRecord::getRemindSent, 0)
                  .le(BorrowRecord::getDueDate, dueDateThreshold.atStartOfDay());
        List<BorrowRecord> comingDueList = borrowRecordMapper.selectList(dueWrapper);

        for (BorrowRecord record : comingDueList) {
            sendComingDueRemind(record);
        }

        // 2. 处理已逾期的借阅
        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING)
                      .lt(BorrowRecord::getDueDate, today.atStartOfDay());
        List<BorrowRecord> overdueList = borrowRecordMapper.selectList(overdueWrapper);

        for (BorrowRecord record : overdueList) {
            sendOverdueRemind(record, today);
        }
    }

    private void sendComingDueRemind(BorrowRecord record) {
        Book book = bookMapper.selectById(record.getBookId());
        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), record.getDueDate().toLocalDate());

        String title = "图书到期提醒";
        String content = String.format("您借阅的《%s》还有%d天到期，请及时归还。", book.getTitle(), daysLeft);

        userMessageService.sendMessage(record.getUserId(), title, content, Constants.MSG_TYPE_BORROW_REMIND);

        // 记录日志
        saveRemindLog(record, Constants.REMIND_TYPE_COMING_DUE, (int) daysLeft);

        // 更新提醒状态
        LambdaUpdateWrapper<BorrowRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BorrowRecord::getId, record.getId())
                     .set(BorrowRecord::getRemindSent, 1);
        borrowRecordMapper.update(null, updateWrapper);
    }

    private void sendOverdueRemind(BorrowRecord record, LocalDate today) {
        Book book = bookMapper.selectById(record.getBookId());
        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(record.getDueDate().toLocalDate(), today);

        // 检查今天是否已经发送过逾期提醒
        LocalDateTime todayStart = today.atStartOfDay();
        LambdaQueryWrapper<BorrowRemindLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(BorrowRemindLog::getBorrowId, record.getId())
                  .eq(BorrowRemindLog::getRemindType, Constants.REMIND_TYPE_OVERDUE)
                  .ge(BorrowRemindLog::getSendTime, todayStart);
        long sentToday = borrowRemindLogMapper.selectCount(logWrapper);

        if (sentToday == 0) {
            String title = "图书逾期提醒";
            String content = String.format("您借阅的《%s》已逾期%d天，请尽快归还！", book.getTitle(), overdueDays);

            userMessageService.sendMessage(record.getUserId(), title, content, Constants.MSG_TYPE_BORROW_REMIND);

            // 更新逾期次数
            LambdaUpdateWrapper<BorrowRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BorrowRecord::getId, record.getId())
                         .set(BorrowRecord::getOverdueRemindCount, record.getOverdueRemindCount() + 1);
            borrowRecordMapper.update(null, updateWrapper);

            // 记录日志
            saveRemindLog(record, Constants.REMIND_TYPE_OVERDUE, (int) overdueDays);

            // Update overdue status
            LambdaUpdateWrapper<BorrowRecord> statusWrapper = new LambdaUpdateWrapper<>();
            statusWrapper.eq(BorrowRecord::getId, record.getId())
                          .set(BorrowRecord::getStatus, Constants.BORROW_STATUS_OVERDUE);
            borrowRecordMapper.update(null, statusWrapper);
        }
    }

    private void saveRemindLog(BorrowRecord record, Integer remindType, Integer remindDays) {
        BorrowRemindLog log = new BorrowRemindLog();
        log.setBorrowId(record.getId());
        log.setUserId(record.getUserId());
        log.setRemindType(remindType);
        log.setRemindDays(remindDays);
        log.setRemindStatus(Constants.REMIND_STATUS_SENT);
        log.setSendTime(LocalDateTime.now());
        borrowRemindLogMapper.insert(log);
    }

    @Override
    public Map<String, Object> getUserBorrowStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 累计借阅
        LambdaQueryWrapper<BorrowRecord> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(BorrowRecord::getUserId, userId);
        long totalBorrow = borrowRecordMapper.selectCount(totalWrapper);
        stats.put("totalBorrowCount", totalBorrow);

        // 当前借阅
        LambdaQueryWrapper<BorrowRecord> currentWrapper = new LambdaQueryWrapper<>();
        currentWrapper.eq(BorrowRecord::getUserId, userId)
                      .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_BORROWING);
        long currentBorrow = borrowRecordMapper.selectCount(currentWrapper);
        stats.put("currentBorrowCount", currentBorrow);

        // 已归还
        LambdaQueryWrapper<BorrowRecord> returnedWrapper = new LambdaQueryWrapper<>();
        returnedWrapper.eq(BorrowRecord::getUserId, userId)
                       .eq(BorrowRecord::getStatus, Constants.BORROW_STATUS_RETURNED);
        long returnedCount = borrowRecordMapper.selectCount(returnedWrapper);
        stats.put("returnedCount", returnedCount);

        // 逾期次数
        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getUserId, userId)
                      .gt(BorrowRecord::getOverdueRemindCount, 0);
        long overdueCount = borrowRecordMapper.selectCount(overdueWrapper);
        stats.put("overdueCount", overdueCount);

        // 分类统计
        stats.put("categoryStats", borrowRecordMapper.getCategoryStats(userId));

        // 月度趋势
        stats.put("monthlyStats", borrowRecordMapper.getMonthlyStats(userId));

        // 借阅排行
        stats.put("topBooks", borrowRecordMapper.getTopBooks(userId));

        return stats;
    }
}
