package com.simulation.fifa.batch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchController {
    @PostMapping("/players")
    public ResponseEntity createPlayers(){
        return ResponseEntity.ok("dd");
    }
}
