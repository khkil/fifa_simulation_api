package com.simulation.fifa.api.league.repository;

import com.simulation.fifa.api.league.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Long> {
}
