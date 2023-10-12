package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.simulation.fifa.api.player.entity.PreferredFootEnum;
import com.simulation.fifa.api.position.dto.PositionDto;
import com.simulation.fifa.api.season.dto.SeasonDto;
import lombok.Data;

import java.util.Set;

@Data
public class PlayerListDto {
    private Long spId;
    private String playerName;
    private Integer pay;
    private PreferredFootEnum preferredFoot;
    private Integer leftFoot;
    private Integer rightFoot;
    private SeasonDto season;
    private Set<PositionDto> positions;

    @QueryProjection
    public PlayerListDto(Long spId, String playerName, Integer pay, PreferredFootEnum preferredFoot, Integer leftFoot, Integer rightFoot, SeasonDto season, Set<PositionDto> positions) {
        this.spId = spId;
        this.playerName = playerName;
        this.pay = pay;
        this.preferredFoot = preferredFoot;
        this.leftFoot = leftFoot;
        this.rightFoot = rightFoot;
        this.season = season;
        this.positions = positions;
    }
}
