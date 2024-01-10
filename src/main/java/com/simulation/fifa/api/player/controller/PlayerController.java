package com.simulation.fifa.api.player.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.player.PlayerService;
import com.simulation.fifa.api.player.dto.PlayerDetailDto;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.dto.PlayerSearchDto;
import com.simulation.fifa.api.price.dto.PlayerPriceWaveDto;
import com.simulation.fifa.api.price.service.PlayerPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerPriceService playerPriceService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findAll(Pageable pageable, PlayerSearchDto playerSearchDto) {
        Page<PlayerListDto> players = playerService.findAll(pageable, playerSearchDto);
        return ResponseEntity.ok(ApiResponse.createSuccess(players));
    }

    @GetMapping("/{spId}")
    public ResponseEntity<ApiResponse<?>> findByOverall(@PathVariable Long spId) {
        PlayerDetailDto playerDetail = playerService.findById(spId);
        return ResponseEntity.ok(ApiResponse.createSuccess(playerDetail));
    }

    @GetMapping("/price-rank")
    public ResponseEntity<ApiResponse<?>> findPriceWave(Pageable pageable) {
        List<PlayerPriceWaveDto> priceRanks = playerPriceService.findPriceRanks(pageable);
        return ResponseEntity.ok(ApiResponse.createSuccess(priceRanks));
    }
}
