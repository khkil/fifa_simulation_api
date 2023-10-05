package com.simulation.fifa.api.association.repository;

import com.simulation.fifa.api.association.entity.PlayerSkillAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerSkillAssociationRepository extends JpaRepository<PlayerSkillAssociation, Long> {
}
