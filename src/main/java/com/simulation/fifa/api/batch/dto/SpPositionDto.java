package com.simulation.fifa.api.batch.dto;

import com.simulation.fifa.api.position.domain.Position;
import lombok.Data;

@Data
public class SpPositionDto {
    private Long spposition;
    private String desc;

    public Position toEntity(SpPositionDto spPositionDto) {
        return Position
                .builder()
                .id(spPositionDto.spposition)
                .positionName(spPositionDto.desc)
                .build();
    }
}
