package com.simulation.fifa.api.player.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.player.PlayerService;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findAll(Pageable pageable) {
        Page<PlayerListDto> players = playerService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.createSuccess(players));
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.findById(id));
    }
}
