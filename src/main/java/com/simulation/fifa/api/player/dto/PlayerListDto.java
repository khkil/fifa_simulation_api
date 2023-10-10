package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PlayerListDto {
    private Long spId;
    private String name;
    private Set<String> positions;

    @QueryProjection
    public PlayerListDto(Long spId, String name, Set<String> positions) {
        this.spId = spId;
        this.name = name;
        this.positions = positions;
    }
}
