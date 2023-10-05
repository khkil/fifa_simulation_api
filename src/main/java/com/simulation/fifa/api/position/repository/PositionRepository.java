package com.simulation.fifa.api.position.repository;

import com.simulation.fifa.api.position.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
