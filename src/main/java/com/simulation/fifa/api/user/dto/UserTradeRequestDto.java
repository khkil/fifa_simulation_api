package com.simulation.fifa.api.user.dto;

import lombok.Data;

@Data
public class UserTradeRequestDto {
    private String tradeType;
    private Integer offset;
    private Integer limit;
}
