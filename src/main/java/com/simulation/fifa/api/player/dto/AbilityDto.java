package com.simulation.fifa.api.player.dto;

import lombok.Data;

public class AbilityDto {
    @Data
    public static class Detail {
        private Long spId;
        private String name;
        private int speed;
        private int acceleration;
        private int finishing;
        private int shootPower;
        private int longShoot;
        private int positioning;
        private int volleyShoot;
        private int penaltyKick;
        private int shortPass;
        private int vision;
        private int crossing;
        private int longPass;
        private int freeKick;
        private int curve;
        private int dribble;
        private int ballControl;
        private int agility;
        private int balance;
        private int reactionSpeed;
        private int defending;
        private int tackling;
        private int interception;
        private int heading;
        private int slideTackle;
        private int physicality;
        private int stamina;
        private int determination;
        private int jumping;
        private int composure;
        private int GKdiving;
        private int GKhandling;
        private int GKkicking;
        private int GKreflexes;
        private int GKpositioning;

        public Detail(Long spId, String name) {
            this.spId = spId;
            this.name = name;
        }

        public void setValueFromText(String text, int value) {
            if(text.equals("속력")){
                this.speed = value;
            }else if(text.equals("가속력")){
                this.acceleration = value;
            }else if(text.equals("골 결정력")){
                this.finishing = value;
            }else if(text.equals("슛 파워")){
                this.shootPower = value;
            }else if(text.equals("중거리 슛")){
                this.longShoot = value;
            }else if(text.equals("위치 선정")){
                this.positioning = value;
            }else if(text.equals("발리슛")){
                this.volleyShoot = value;
            }else if(text.equals("페널티 킥")){
                this.penaltyKick = value;
            }else if(text.equals("짧은 패스")){
                this.shortPass = value;
            }else if(text.equals("시야")){
                this.vision = value;
            }else if(text.equals("크로스")){
                this.crossing = value;
            }else if(text.equals("긴 패스")){
                this.longPass = value;
            }else if(text.equals("프리킥")){
                this.freeKick = value;
            }else if(text.equals("커브")){
                this.curve = value;
            }else if(text.equals("드리블")){
                this.dribble = value;
            }else if(text.equals("볼 컨트롤")){
                this.ballControl = value;
            }else if(text.equals("민첩성")){
                this.agility = value;
            }else if(text.equals("밸런스")){
                this.balance = value;
            }else if(text.equals("반응 속도")){
                this.reactionSpeed = value;
            }else if(text.equals("대인 수비")){
                this.defending = value;
            }else if(text.equals("태클")){
                this.tackling = value;
            }else if(text.equals("가로채기")){
                this.interception = value;
            }else if(text.equals("헤더")){
                this.heading = value;
            }else if(text.equals("슬라이딩 태클")){
                this.slideTackle = value;
            }else if(text.equals("몸싸움")){
                this.physicality = value;
            }else if(text.equals("스태미너")){
                this.stamina = value;
            }else if(text.equals("적극성")){
                this.determination = value;
            }else if(text.equals("점프")){
                this.jumping = value;
            }else if(text.equals("침착성")){
                this.composure = value;
            }else if(text.equals("GK 다이빙")){
                this.GKdiving = value;
            }else if(text.equals("GK 핸들링")){
                this.GKhandling = value;
            }else if(text.equals("GK 킥")){
                this.GKkicking = value;
            }else if(text.equals("GK 반응속도")){
                this.GKreflexes = value;
            }else if(text.equals("GK 위치 선정")){
                this.GKpositioning = value;
            }
        }
    }

    public static class Average {

    }


}
