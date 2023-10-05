package com.simulation.fifa.api.skill.repository;

import com.simulation.fifa.api.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
