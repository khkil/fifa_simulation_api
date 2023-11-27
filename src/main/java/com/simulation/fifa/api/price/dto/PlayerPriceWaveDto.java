package com.simulation.fifa.api.price.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class PlayerPriceWaveDto {
    private long playerId;
    private String playerName;
    private long yesterdayPrice;
    private long todayPrice;
    private long percentage;
    private String seasonImgUrl;

    @QueryProjection
    public PlayerPriceWaveDto(long playerId, String playerName, long yesterdayPrice, long todayPrice, long percentage, String seasonImgUrl) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.yesterdayPrice = yesterdayPrice;
        this.todayPrice = todayPrice;
        this.percentage = percentage;
        this.seasonImgUrl = seasonImgUrl;
    }
}
