package com.simulation.fifa.api.price.repository;

import com.simulation.fifa.api.price.dto.PlayerRecentPriceDto;

import java.util.List;

public interface PlayerPriceRepositoryCustom {
    List<PlayerRecentPriceDto> findRecentPriceList(List<Long> playerIds, List<Integer> grades);
}
