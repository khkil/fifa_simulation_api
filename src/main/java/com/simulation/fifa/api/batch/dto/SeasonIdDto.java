package com.simulation.fifa.api.batch.dto;

import com.simulation.fifa.api.season.entity.Season;
import lombok.Data;

@Data
public class SeasonIdDto {
    private Long seasonId;
    private String className;
    private String seasonImg;

    public Season toEntity(SeasonIdDto seasonIdDto) {
        return Season
                .builder()
                .id(seasonIdDto.seasonId)
                .name(seasonIdDto.className)
                .imageUrl(seasonIdDto.seasonImg)
                .build();
    }
}
