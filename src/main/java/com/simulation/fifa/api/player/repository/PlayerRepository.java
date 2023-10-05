package com.simulation.fifa.api.player.repository;

import com.simulation.fifa.api.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
