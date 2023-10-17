package com.simulation.fifa.api.price.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PlayerPriceListDto {
    private Long price;
    private Integer grade;

    @QueryProjection
    public PlayerPriceListDto(Long price, Integer grade) {
        this.price = price;
        this.grade = grade;
    }
}
