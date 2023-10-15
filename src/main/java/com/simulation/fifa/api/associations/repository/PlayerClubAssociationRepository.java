package com.simulation.fifa.api.associations.repository;

import com.simulation.fifa.api.associations.entity.PlayerClubAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerClubAssociationRepository extends JpaRepository<PlayerClubAssociation, Long> {
}
