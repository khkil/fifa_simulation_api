package com.simulation.fifa.api.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.simulation.fifa.api.season.dto.SeasonListDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTradeListDto {
    private LocalDateTime tradeDate;
    private String saleSn;
    private Long spid;
    private String playerName;
    private SeasonListDto season;
    private Integer grade;
    private Long value;
}
