package com.simulation.fifa.api.season.repository;

import com.simulation.fifa.api.season.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonRepository extends JpaRepository<Season, Long> {
}
