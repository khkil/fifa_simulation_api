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

    @PostMapping("/players")
    public ResponseEntity createPlayers(){
        batchService.createSeasons();
        batchService.createPlayers();
        return ResponseEntity.ok("dd");
    }
}
