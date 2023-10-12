package com.simulation.fifa.api.skill.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.skill.dto.QSkillListDto;
import com.simulation.fifa.api.skill.dto.SkillListDto;
import com.simulation.fifa.api.skill.dto.SkillSearchDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.simulation.fifa.api.skill.entity.QSkill.skill;

@RequiredArgsConstructor
public class SkillRepositoryImpl implements SkillRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SkillListDto> findAllCustom() {
        return jpaQueryFactory
                .select(new QSkillListDto(
                        skill.id,
                        skill.skillName
                ))
                .from(skill)
                .orderBy(skill.id.asc())
                .fetch();
    }
}
