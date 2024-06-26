package com.simulation.fifa.api.platform.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTradeRequestDto {
    private String tradeType;
    private Integer offset;
    private Integer limit;
}
