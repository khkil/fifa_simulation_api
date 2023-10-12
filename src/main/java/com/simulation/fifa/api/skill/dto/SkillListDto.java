package com.simulation.fifa.api.skill.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class SkillListDto {
    private Long id;
    private String skillName;

    @QueryProjection
    public SkillListDto(Long id, String skillName) {
        this.id = id;
        this.skillName = skillName;
    }
}
