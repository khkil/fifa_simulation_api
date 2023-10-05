package com.simulation.fifa.api.batch.controller;

import com.simulation.fifa.api.batch.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchController {
    @Autowired
    BatchService batchService;

    @PostMapping
    public ResponseEntity createNations() {
        return ResponseEntity.ok("nation");
    }

    @PostMapping("/clubs")
    public ResponseEntity createClubs() {
        batchService.createLeagues();
        batchService.createClubs();
        return ResponseEntity.ok("league");
    }

    @PostMapping("/players")
    public ResponseEntity createPlayers() {
        batchService.createSeasons();
        batchService.createPositions();
        batchService.createPlayers();
        return ResponseEntity.ok("dd");
    }

    @PostMapping("/all")
    public ResponseEntity createAll() {
        batchService.createLeagues();
        batchService.createClubs();
        batchService.createSeasons();
        batchService.createPositions();
        batchService.createSkills();
        batchService.createPlayers();
        return ResponseEntity.ok("dd");
    }
}
