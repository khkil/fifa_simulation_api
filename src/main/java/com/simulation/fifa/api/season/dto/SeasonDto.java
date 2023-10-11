package com.simulation.fifa.api.season.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class SeasonDto {
    private Long id;
    private String seasonName;
    private String imageUrl;

    @QueryProjection
    public SeasonDto(Long id, String seasonName, String imageUrl) {
        this.id = id;
        this.seasonName = seasonName;
        this.imageUrl = imageUrl;
    }
}
