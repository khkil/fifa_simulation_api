package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.simulation.fifa.api.club.dto.ClubListDto;
import com.simulation.fifa.api.position.dto.PositionDto;
import com.simulation.fifa.api.season.dto.SeasonListDto;
import lombok.Data;

import java.util.Set;

@Data
public class PlayerDetailDto {
    private Long id;
    private String name;
    private SeasonListDto season;
    private Set<PositionDto> positions;
    private Set<ClubListDto> clubs;

    @QueryProjection
    public PlayerDetailDto(Long id, String name, SeasonListDto season, Set<PositionDto> positions, Set<ClubListDto> clubs) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.positions = positions;
        this.clubs = clubs;
    }
}
