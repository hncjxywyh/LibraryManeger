package com.library.task;

import com.library.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationExpireTask {

    private final ReservationService reservationService;

    /**
     * 每天凌晨2点检查过期预约
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processExpiredReservations() {
        reservationService.processExpiredReservations();
    }
}
