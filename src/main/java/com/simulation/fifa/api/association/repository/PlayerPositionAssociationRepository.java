package com.simulation.fifa.api.association.repository;

import com.simulation.fifa.api.association.entity.PlayerPositionAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerPositionAssociationRepository extends JpaRepository<PlayerPositionAssociation, Long> {
}
