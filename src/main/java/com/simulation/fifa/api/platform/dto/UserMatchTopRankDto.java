package com.simulation.fifa.api.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserMatchTopRankDto {

    @Data
    public static class origin {
        private Integer matchType;
        private Integer division;
        private LocalDateTime achievementDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class desc {
        private String matchTypeDesc;
        private String divisionName;
        private String divisionImageUrl;
        private LocalDateTime achievementDate;
    }
}
