package com.simulation.fifa.api.player.dto;

import lombok.Data;

@Data
public class PlayerSearchDto {
    private Long[] clubIds;
    private Long[] skillIds;
    private Long[] seasonIds;
    private Long[] nationIds;
    private String name;
}
