package com.simulation.fifa.api.player.dto;

import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.entity.PreferredFootEnum;
import lombok.Data;

@Data
public class PlayerBatchDto {
    private Long spId;
    private String name;
    // 능력치
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
    private Integer interception;
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
    // 주발 약발
    private PreferredFootEnum preferredFoot;
    private Integer rightFoot;
    private Integer leftFoot;
    // 급여
    private Integer pay;

    public PlayerBatchDto(Long spId, String name) {
        this.spId = spId;
        this.name = name;
    }

    public Player toEntity(PlayerBatchDto playerBatchDto) {
        return Player
                .builder()
                .id(playerBatchDto.spId)
                .name(playerBatchDto.name)
                .speed(playerBatchDto.speed)
                .acceleration(playerBatchDto.acceleration)
                .finishing(playerBatchDto.finishing)
                .shootPower(playerBatchDto.shootPower)
                .longShoot(playerBatchDto.longShoot)
                .positioning(playerBatchDto.positioning)
                .volleyShoot(playerBatchDto.volleyShoot)
                .penaltyKick(playerBatchDto.penaltyKick)
                .shortPass(playerBatchDto.shortPass)
                .vision(playerBatchDto.vision)
                .crossing(playerBatchDto.crossing)
                .longPass(playerBatchDto.longPass)
                .freeKick(playerBatchDto.freeKick)
                .curve(playerBatchDto.curve)
                .dribble(playerBatchDto.dribble)
                .ballControl(playerBatchDto.ballControl)
                .agility(playerBatchDto.agility)
                .balance(playerBatchDto.balance)
                .reactionSpeed(playerBatchDto.reactionSpeed)
                .defending(playerBatchDto.defending)
                .tackling(playerBatchDto.tackling)
                .interception(playerBatchDto.interception)
                .heading(playerBatchDto.heading)
                .slideTackle(playerBatchDto.slideTackle)
                .physicality(playerBatchDto.physicality)
                .stamina(playerBatchDto.stamina)
                .determination(playerBatchDto.determination)
                .jumping(playerBatchDto.jumping)
                .composure(playerBatchDto.composure)
                .gkDiving(playerBatchDto.gkDiving)
                .gkHandling(playerBatchDto.gkHandling)
                .gkKicking(playerBatchDto.gkKicking)
                .gkReflexes(playerBatchDto.gkReflexes)
                .gkPositioning(playerBatchDto.gkPositioning)
                .preferredFoot(preferredFoot)
                .rightFoot(rightFoot)
                .leftFoot(leftFoot)
                .pay(pay)
                .build();
    }

    public void setValueFromText(String text, Integer value) {
        switch (text) {
            // 스피드
            case "속력" -> this.speed = value;
            case "가속력" -> this.acceleration = value;
            // 슛팅
            case "골 결정력" -> this.finishing = value;
            case "슛 파워" -> this.shootPower = value;
            case "중거리 슛" -> this.longShoot = value;
            case "위치 선정" -> this.positioning = value;
            case "발리슛" -> this.volleyShoot = value;
            case "페널티 킥" -> this.penaltyKick = value;
            // 패스
            case "짧은 패스" -> this.shortPass = value;
            case "시야" -> this.vision = value;
            case "크로스" -> this.crossing = value;
            case "긴 패스" -> this.longPass = value;
            case "프리킥" -> this.freeKick = value;
            case "커브" -> this.curve = value;
            // 드리블
            case "드리블" -> this.dribble = value;
            case "볼 컨트롤" -> this.ballControl = value;
            case "민첩성" -> this.agility = value;
            case "밸런스" -> this.balance = value;
            case "반응 속도" -> this.reactionSpeed = value;
            // 수비
            case "대인 수비" -> this.defending = value;
            case "태클" -> this.tackling = value;
            case "가로채기" -> this.interception = value;
            case "헤더" -> this.heading = value;
            case "슬라이딩 태클" -> this.slideTackle = value;
            // 피지컬
            case "몸싸움" -> this.physicality = value;
            case "스태미너" -> this.stamina = value;
            case "적극성" -> this.determination = value;
            case "점프" -> this.jumping = value;
            //
            case "침착성" -> this.composure = value;
            // 골키퍼
            case "GK 다이빙" -> this.gkDiving = value;
            case "GK 핸들링" -> this.gkHandling = value;
            case "GK 킥" -> this.gkKicking = value;
            case "GK 반응속도" -> this.gkReflexes = value;
            case "GK 위치 선정" -> this.gkPositioning = value;
            //
        }
    }
}
