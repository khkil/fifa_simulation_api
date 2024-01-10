package com.simulation.fifa.api.player.repository;

import com.simulation.fifa.api.batch.dto.CheckPlayerPriceDto;
import com.simulation.fifa.api.player.dto.PlayerDetailDto;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.dto.PlayerSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlayerRepositoryCustom {
    Page<PlayerListDto> findAllCustom(Pageable pageable, PlayerSearchDto playerSearchDto);

    Optional<PlayerDetailDto> findByIdCustom(Long id);

    List<CheckPlayerPriceDto> findCheckPrice();
}
