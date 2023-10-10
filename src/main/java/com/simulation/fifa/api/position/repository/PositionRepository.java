package com.simulation.fifa.api.position.repository;

import com.simulation.fifa.api.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
