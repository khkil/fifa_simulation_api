package com.simulation.fifa.api.position.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PositionDto {
    private String positionName;
    private Integer stat;

    @QueryProjection
    public PositionDto(String positionName, Integer stat) {
        this.positionName = positionName;
        this.stat = stat;
    }
}
