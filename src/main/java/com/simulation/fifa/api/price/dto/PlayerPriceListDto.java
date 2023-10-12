package com.simulation.fifa.api.price.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PlayerPriceListDto {
    private Long price;
    private Integer upgradeValue;

    @QueryProjection
    public PlayerPriceListDto(Long price, Integer upgradeValue) {
        this.price = price;
        this.upgradeValue = upgradeValue;
    }
}
