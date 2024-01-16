package com.simulation.fifa.api.price.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.simulation.fifa.api.position.dto.PositionDto;
import com.simulation.fifa.api.season.dto.SeasonListDto;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PriceOverallDto {
    private Long spId;
    private String playerName;
    private Integer overall;
    private Long price;
    private Integer grade;
    private SeasonListDto season;
    private Set<PositionDto> positions;

    @QueryProjection
    public PriceOverallDto(Long spId, String playerName, Integer overall, Long price, Integer grade, SeasonListDto season, Set<PositionDto> positions) {
        this.spId = spId;
        this.playerName = playerName;
        this.overall = overall;
        this.price = price;
        this.grade = grade;
        this.season = season;
        this.positions = positions;
    }
}
