package com.simulation.fifa.api.player.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PlayerListDto {
    private Long spId;
    private String name;

    @QueryProjection
    public PlayerListDto(Long spId, String name) {
        this.spId = spId;
        this.name = name;
    }
}
