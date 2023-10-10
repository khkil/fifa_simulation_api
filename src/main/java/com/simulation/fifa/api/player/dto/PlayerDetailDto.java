package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.simulation.fifa.api.club.dto.ClubDto;
import com.simulation.fifa.api.position.dto.PositionDto;
import com.simulation.fifa.api.season.dto.SeasonDto;
import lombok.Data;

import java.util.Set;

@Data
public class PlayerDetailDto {
    private Long id;
    private String name;
    private SeasonDto season;
    private Set<PositionDto> positions;
    private Set<ClubDto> clubs;

    @QueryProjection
    public PlayerDetailDto(Long id, String name, SeasonDto season, Set<PositionDto> positions, Set<ClubDto> clubs) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.positions = positions;
        this.clubs = clubs;
    }
}
