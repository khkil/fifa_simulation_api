package com.simulation.fifa.api.player.controller;

import com.simulation.fifa.api.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @GetMapping
    public ResponseEntity findAll(Pageable pageable) {
        return ResponseEntity.ok(playerService.findAll(pageable));
    }
}
