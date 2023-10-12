package com.simulation.fifa.api.skill.repository;

import com.simulation.fifa.api.skill.dto.SkillListDto;

import java.util.List;

public interface SkillRepositoryCustom {
    List<SkillListDto> findAllCustom();
}
