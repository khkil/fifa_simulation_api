package com.simulation.fifa.api.nation.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class NationListDto {
    private Long id;
    private String nationName;

    @QueryProjection
    public NationListDto(Long id, String nationName) {
        this.id = id;
        this.nationName = nationName;
    }
}
