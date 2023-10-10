package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
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
    private Set<Position> positions;

    @QueryProjection
    public PlayerListDto(Long spId, String name, SeasonDto season, Set<Position> positions) {
        this.spId = spId;
        this.name = name;
        this.season = season;
        this.positions = positions;
    }

    @Data
    public static class Position {
        private String name;
        private Integer stat;

        @QueryProjection
        public Position(String name, Integer stat) {
            this.name = name;
            this.stat = stat;
        }
    }
}
