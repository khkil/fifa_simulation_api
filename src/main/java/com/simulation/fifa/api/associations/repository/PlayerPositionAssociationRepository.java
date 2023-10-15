package com.simulation.fifa.api.associations.repository;

import com.simulation.fifa.api.associations.entity.PlayerPositionAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerPositionAssociationRepository extends JpaRepository<PlayerPositionAssociation, Long> {
}
