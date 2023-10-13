package com.simulation.fifa.api.batch.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CheckPlayerPriceDto {
    private Long playerId;
    private List<Date> dateList;

    @QueryProjection
    public CheckPlayerPriceDto(Long playerId, List<Date> dateList) {
        this.playerId = playerId;
        this.dateList = dateList;
    }

    @Data
    public static class Date {
        private LocalDate date;
        private Long count;

        @QueryProjection
        public Date(LocalDate date, Long count) {
            this.date = date;
            this.count = count;
        }
    }
}
