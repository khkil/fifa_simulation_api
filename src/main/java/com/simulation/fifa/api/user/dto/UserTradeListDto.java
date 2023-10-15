package com.simulation.fifa.api.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class UserTradeListDto {
    private String tradeDate;
    private String saleSn;
    private Long spid;
    private Integer grade;
    private Long value;
}
