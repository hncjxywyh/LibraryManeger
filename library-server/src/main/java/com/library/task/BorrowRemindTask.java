package com.library.task;

import com.library.service.BorrowRemindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowRemindTask {

    private final BorrowRemindService borrowRemindService;

    // 每天早上9点执行
    @Scheduled(cron = "0 0 9 * * ?")
    public void executeRemindTask() {
        log.info("开始执行借阅提醒任务");
        try {
            borrowRemindService.processRemindTask();
            log.info("借阅提醒任务执行完成");
        } catch (Exception e) {
            log.error("借阅提醒任务执行失败", e);
        }
    }
}
