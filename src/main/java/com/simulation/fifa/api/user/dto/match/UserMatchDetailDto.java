package com.simulation.fifa.api.user.dto.match;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserMatchDetailDto {
    private String matchId;
    private LocalDateTime matchDate;
    private Integer matchType;

    private List<MatchInfo> matchInfo;

    @Data
    public static class MatchInfo {
        private String accessId;
        private String nickname;
        private MatchDetail matchDetail;
        private List<Player> player;

        @Data
        public static class MatchDetail {

        }

        @Data
        public static class Player {
            private Long spId;
            private Long spPosition;
            private Integer spGrade;
        }
    }
}
