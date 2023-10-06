package com.simulation.fifa.api.player.repository;

import com.simulation.fifa.api.player.dto.PlayerListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlayerRepositoryCustom {
    Page<PlayerListDto> findAllCustom(Pageable pageable);
}
