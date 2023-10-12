package com.simulation.fifa.api.position.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PositionDto {
    private String positionName;
    private Integer overall;

    @QueryProjection
    public PositionDto(String positionName, Integer overall) {
        this.positionName = positionName;
        this.overall = overall;
    }
}
