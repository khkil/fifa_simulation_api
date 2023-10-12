package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.simulation.fifa.api.player.entity.PreferredFootEnum;
import com.simulation.fifa.api.position.dto.PositionDto;
import com.simulation.fifa.api.price.dto.PlayerPriceListDto;
import com.simulation.fifa.api.season.dto.SeasonDto;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PlayerListDto {
    private Long spId;
    private String playerName;
    private Integer pay;
    private PreferredFootEnum preferredFoot;
    private Integer leftFoot;
    private Integer rightFoot;
    private Set<PlayerPriceListDto> priceList;
    private Average average;
    private SeasonDto season;
    private Set<PositionDto> positions;

    @QueryProjection
    public PlayerListDto(Long spId, String playerName, Integer pay, PreferredFootEnum preferredFoot, Integer leftFoot, Integer rightFoot, Set<PlayerPriceListDto> priceList, Average average, SeasonDto season, Set<PositionDto> positions) {
        this.spId = spId;
        this.playerName = playerName;
        this.pay = pay;
        this.preferredFoot = preferredFoot;
        this.leftFoot = leftFoot;
        this.rightFoot = rightFoot;
        this.priceList = priceList;
        this.average = average;
        this.season = season;
        this.positions = positions;
    }

    @Data
    public static class Average {
        private Integer speed;
        private Integer shooting;
        private Integer passing;
        private Integer dribble;
        private Integer defending;
        private Integer physical;

        @QueryProjection
        public Average(Integer speed, Integer shooting, Integer passing, Integer dribble, Integer defending, Integer physical) {
            this.speed = speed;
            this.shooting = shooting;
            this.passing = passing;
            this.dribble = dribble;
            this.defending = defending;
            this.physical = physical;
        }
    }
}
