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
            private int matchEndType;
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
            private String seasonImageUrl;
            private long spPosition;
            private String positionName;
            private int spGrade;
            private long price;
            private Status status;

            @Data
            public static class Status {
                private double spRating;
                private int goal;
                private int shoot;
                private int passTry;
                private int passSuccess;
                private int dribbleTry;
                private int dribbleSuccess;
                private int aerialTry;
                private int aerialSuccess;
                private int blockTry;
                private int block;
                private int tackleTry;
                private int tackle;

                /*private int dribble;
                private int dribbleSuccess;
                private int defending;
                private int passSuccess;
                private int aerialSuccess;
                private int tackle;*/
            }
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
