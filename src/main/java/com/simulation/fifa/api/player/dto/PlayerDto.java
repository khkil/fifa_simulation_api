package com.simulation.fifa.api.player.dto;

import com.simulation.fifa.api.player.entity.Player;
import lombok.Data;

public class PlayerDto {
    @Data
    public static class Detail {
        private Long spId;
        private String name;
        private Integer speed;
        private Integer acceleration;
        private Integer finishing;
        private Integer shootPower;
        private Integer longShoot;
        private Integer positioning;
        private Integer volleyShoot;
        private Integer penaltyKick;
        private Integer shortPass;
        private Integer vision;
        private Integer crossing;
        private Integer longPass;
        private Integer freeKick;
        private Integer curve;
        private Integer dribble;
        private Integer ballControl;
        private Integer agility;
        private Integer balance;
        private Integer reactionSpeed;
        private Integer defending;
        private Integer tackling;
        private Integer Integererception;
        private Integer heading;
        private Integer slideTackle;
        private Integer physicality;
        private Integer stamina;
        private Integer determination;
        private Integer jumping;
        private Integer composure;
        private Integer gkDiving;
        private Integer gkHandling;
        private Integer gkKicking;
        private Integer gkReflexes;
        private Integer gkPositioning;

        public Detail(Long spId, String name) {
            this.spId = spId;
            this.name = name;
        }

        public Player toEntity(Detail detail) {
            return Player
                    .builder()
                    .id(detail.spId)
                    .name(detail.name)
                    .speed(detail.speed)
                    .acceleration(detail.acceleration)
                    .finishing(detail.finishing)
                    .shootPower(detail.shootPower)
                    .longShoot(detail.longShoot)
                    .positioning(detail.positioning)
                    .volleyShoot(detail.volleyShoot)
                    .penaltyKick(detail.penaltyKick)
                    .shortPass(detail.shortPass)
                    .vision(detail.vision)
                    .crossing(detail.crossing)
                    .longPass(detail.longPass)
                    .freeKick(detail.freeKick)
                    .curve(detail.curve)
                    .dribble(detail.dribble)
                    .ballControl(detail.ballControl)
                    .agility(detail.agility)
                    .balance(detail.balance)
                    .reactionSpeed(detail.reactionSpeed)
                    .defending(detail.defending)
                    .tackling(detail.tackling)
                    .Integererception(detail.Integererception)
                    .heading(detail.heading)
                    .slideTackle(detail.slideTackle)
                    .physicality(detail.physicality)
                    .stamina(detail.stamina)
                    .determination(detail.determination)
                    .jumping(detail.jumping)
                    .composure(detail.composure)
                    .gkDiving(detail.gkDiving)
                    .gkHandling(detail.gkHandling)
                    .gkKicking(detail.gkKicking)
                    .gkReflexes(detail.gkReflexes)
                    .gkPositioning(detail.gkPositioning)
                    .build();
        }

        public void setValueFromText(String text, Integer value) {
            if (text.equals("속력")) {
                this.speed = value;
            } else if (text.equals("가속력")) {
                this.acceleration = value;
            } else if (text.equals("골 결정력")) {
                this.finishing = value;
            } else if (text.equals("슛 파워")) {
                this.shootPower = value;
            } else if (text.equals("중거리 슛")) {
                this.longShoot = value;
            } else if (text.equals("위치 선정")) {
                this.positioning = value;
            } else if (text.equals("발리슛")) {
                this.volleyShoot = value;
            } else if (text.equals("페널티 킥")) {
                this.penaltyKick = value;
            } else if (text.equals("짧은 패스")) {
                this.shortPass = value;
            } else if (text.equals("시야")) {
                this.vision = value;
            } else if (text.equals("크로스")) {
                this.crossing = value;
            } else if (text.equals("긴 패스")) {
                this.longPass = value;
            } else if (text.equals("프리킥")) {
                this.freeKick = value;
            } else if (text.equals("커브")) {
                this.curve = value;
            } else if (text.equals("드리블")) {
                this.dribble = value;
            } else if (text.equals("볼 컨트롤")) {
                this.ballControl = value;
            } else if (text.equals("민첩성")) {
                this.agility = value;
            } else if (text.equals("밸런스")) {
                this.balance = value;
            } else if (text.equals("반응 속도")) {
                this.reactionSpeed = value;
            } else if (text.equals("대인 수비")) {
                this.defending = value;
            } else if (text.equals("태클")) {
                this.tackling = value;
            } else if (text.equals("가로채기")) {
                this.Integererception = value;
            } else if (text.equals("헤더")) {
                this.heading = value;
            } else if (text.equals("슬라이딩 태클")) {
                this.slideTackle = value;
            } else if (text.equals("몸싸움")) {
                this.physicality = value;
            } else if (text.equals("스태미너")) {
                this.stamina = value;
            } else if (text.equals("적극성")) {
                this.determination = value;
            } else if (text.equals("점프")) {
                this.jumping = value;
            } else if (text.equals("침착성")) {
                this.composure = value;
            } else if (text.equals("GK 다이빙")) {
                this.gkDiving = value;
            } else if (text.equals("GK 핸들링")) {
                this.gkHandling = value;
            } else if (text.equals("GK 킥")) {
                this.gkKicking = value;
            } else if (text.equals("GK 반응속도")) {
                this.gkReflexes = value;
            } else if (text.equals("GK 위치 선정")) {
                this.gkPositioning = value;
            }
        }
    }

    public static class Average {

    }


}
