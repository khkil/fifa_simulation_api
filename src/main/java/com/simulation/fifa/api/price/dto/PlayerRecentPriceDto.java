package com.simulation.fifa.api.price.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayerRecentPriceDto {
    private Long playerId;
    private Long price;
    private Integer grade;

    @QueryProjection
    public PlayerRecentPriceDto(Long playerId, Long price, Integer grade) {
        this.playerId = playerId;
        this.price = price;
        this.grade = grade;
    }

    @Data
    public static class PlayerRecentDate {
        private Long PlayerId;
        private LocalDate date;

        @QueryProjection
        public PlayerRecentDate(Long playerId, LocalDate date) {
            PlayerId = playerId;
            this.date = date;
        }
    }
}
