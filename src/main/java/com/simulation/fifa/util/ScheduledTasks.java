package com.simulation.fifa.util;

import com.simulation.fifa.api.batch.dto.CheckPlayerPriceDto;
import com.simulation.fifa.api.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final BatchService batchService;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0 09 * * *")
    public void renewalPrice() {
        log.info("시세 갱신 시작 {}", dateFormat.format(new Date()));

        batchService.deletePreviousPrice();
        batchService.createPrice(LocalDate.now());

        log.info("시세 갱신 완료 {}", dateFormat.format(new Date()));
    }
}
