package com.simulation.fifa.api.association.repository;

import com.simulation.fifa.api.association.entity.PlayerClubAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerClubAssociationRepository extends JpaRepository<PlayerClubAssociation, Long> {
}
