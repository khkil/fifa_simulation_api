package com.simulation.fifa.api.player.repository;

import com.simulation.fifa.api.player.entity.Player;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long>, PlayerRepositoryCustom {

}
