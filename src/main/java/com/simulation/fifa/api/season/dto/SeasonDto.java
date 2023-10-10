package com.simulation.fifa.api.season.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class SeasonDto {
    private Long id;
    private String name;
    private String imageUrl;

    @QueryProjection
    public SeasonDto(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
