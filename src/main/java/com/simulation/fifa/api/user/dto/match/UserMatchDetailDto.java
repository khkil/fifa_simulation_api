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
        private Shoot shoot;
        private Pass pass;
        private Defence defence;
        private List<Player> player;

        @Data
        public static class MatchDetail {
            private String matchResult;
            private String controller;
            private int possession;
            private int cornerKick;
            private int foul;
            private int offsideCount;
            private int yellowCards;
            private int redCards;
            private int injury;
        }

        @Data
        public static class Player {
            private long spId;
            private String name;
            private long seasonId;
            private long spPosition;
            private String positionName;
            private int spGrade;
            private long price;
        }

        @Data
        public static class Shoot {
            private int shootTotal;
            private int effectiveShootTotal;
            private int goalTotal;
        }

        @Data
        public static class Pass {
            private int passTry;
            private int passSuccess;
        }

        @Data
        public static class Defence {
            private int tackleTry;
            private int tackleSuccess;
        }
    }
}
