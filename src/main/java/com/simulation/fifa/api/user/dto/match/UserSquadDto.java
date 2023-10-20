package com.simulation.fifa.api.user.dto.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSquadDto {
    private Long playerId;
    private String playerName;
    private Integer grade;
    private String positionName;
    private Integer pay;
    private Long seasonId;
    private String seasonName;
    private String seasonImgUrl;
    private Long recentPrice;
}
