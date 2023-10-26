package com.simulation.fifa.api.user.dto.match;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserMatchListDto {
    private String matchId;
    private LocalDateTime matchDate;
    private Integer matchType;

    private List<User> users;

    @Data
    @Builder
    public static class User {
        private String accessId;
        private String nickname;
        private String matchResult;
        private Integer goal;
        private String controller;
    }
}
