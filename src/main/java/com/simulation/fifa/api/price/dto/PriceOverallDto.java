package com.simulation.fifa.api.price.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PriceOverallDto {
    String playerName;
    Integer overall;
    Long price;

    @QueryProjection
    public PriceOverallDto(String playerName, Integer overall, Long price) {
        this.playerName = playerName;
        this.overall = overall;
        this.price = price;
    }
}
