package com.simulation.fifa.api.price.repository;

import com.simulation.fifa.api.price.dto.PlayerPriceWaveDto;
import com.simulation.fifa.api.user.dto.squad.SquadDto;
import com.simulation.fifa.api.price.dto.PlayerRecentPriceDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PlayerPriceRepositoryCustom {
    List<PlayerRecentPriceDto> findRecentPriceList(List<Long> playerIds, List<Integer> grades);

    List<Long> findByNotRenewalPrice(LocalDate localDate);

    long deletePreviousPrice(LocalDate previousDate);

    List<SquadDto.TotalPrice> findPlayerPriceByIdsAndDateBetween(List<SquadDto.Player> players, LocalDate start, LocalDate end);

    List<PlayerPriceWaveDto> findPlayerPriceWave(Pageable pageable);

}
