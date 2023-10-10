package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.simulation.fifa.api.position.dto.PositionDto;
import com.simulation.fifa.api.position.entity.Position;
import com.simulation.fifa.api.season.dto.SeasonDto;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PlayerListDto {
    private Long spId;
    private String name;
    private SeasonDto season;
    private Set<PositionDto> positions;

    @QueryProjection
    public PlayerListDto(Long spId, String name, SeasonDto season, Set<PositionDto> positions) {
        this.spId = spId;
        this.name = name;
        this.season = season;
        this.positions = positions;
    }
}
