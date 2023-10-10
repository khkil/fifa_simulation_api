package com.simulation.fifa.api.position.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PositionDto {
    private String name;
    private Integer stat;

    @QueryProjection
    public PositionDto(String name, Integer stat) {
        this.name = name;
        this.stat = stat;
    }
}
