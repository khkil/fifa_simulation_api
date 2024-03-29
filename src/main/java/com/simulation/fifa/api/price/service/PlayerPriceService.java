package com.simulation.fifa.api.price.service;

import com.simulation.fifa.api.price.dto.PlayerPriceWaveDto;
import com.simulation.fifa.api.player.dto.PlayerByOverallDto;
import com.simulation.fifa.api.price.repository.PlayerPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerPriceService {
    private final PlayerPriceRepository playerPriceRepository;

    public List<PlayerPriceWaveDto> findPriceRanks(Pageable pageable) {
        return playerPriceRepository.findPriceRanks(pageable);
    }
}
