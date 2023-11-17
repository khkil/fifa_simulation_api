package com.simulation.fifa.api.price.repository;

import com.simulation.fifa.api.player.dto.SquadDto;
import com.simulation.fifa.api.price.dto.PlayerRecentPriceDto;

import java.time.LocalDate;
import java.util.List;

public interface PlayerPriceRepositoryCustom {
    List<PlayerRecentPriceDto> findRecentPriceList(List<Long> playerIds, List<Integer> grades);

    List<Long> findByNotRenewalPrice(LocalDate localDate);

    long deletePreviousPrice(LocalDate previousDate);

    List<SquadDto.TotalPrice> findPlayerPriceByIdsAndDateBetween(List<SquadDto.Player> players, LocalDate start, LocalDate end);

}
