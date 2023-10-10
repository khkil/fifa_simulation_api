package com.simulation.fifa.api.club.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ClubDto {
    private Long id;
    private String clubName;

    @QueryProjection
    public ClubDto(Long id, String clubName) {
        this.id = id;
        this.clubName = clubName;
    }
}
