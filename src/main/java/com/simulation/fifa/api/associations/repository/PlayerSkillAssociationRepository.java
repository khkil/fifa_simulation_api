package com.simulation.fifa.api.associations.repository;

import com.simulation.fifa.api.associations.entity.PlayerSkillAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerSkillAssociationRepository extends JpaRepository<PlayerSkillAssociation, Long> {
}
