package com.simulation.fifa.api.batch.controller;

import com.simulation.fifa.api.batch.service.BatchService;
import com.simulation.fifa.api.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchController {
    @Autowired
    BatchService batchService;

    @PostMapping("/nations")
    public ResponseEntity<ApiResponse<?>> createNations() {
        batchService.createNations();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("국가 생성 성공"));
    }

    @PostMapping("/clubs")
    public ResponseEntity<ApiResponse<?>> createClubs() {
        batchService.createLeagues();
        batchService.createClubs();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("리그 & 클럽 생성 성공"));
    }

    @PostMapping("/players")
    public ResponseEntity<ApiResponse<?>> createPlayers() {
        batchService.createPlayers();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("선수 생성 성공"));
    }

    @PostMapping("/all")
    public ResponseEntity<ApiResponse<?>> createAll() {
        batchService.createLeagues();
        batchService.createClubs();
        batchService.createNations();
        batchService.createSeasons();
        batchService.createPositions();
        batchService.createSkills();
        batchService.createPlayers();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("전체 데이터 생성 성공"));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<?>> bulk() {
        batchService.bulkTest();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("bulk test 성공"));
    }

    @PutMapping("/price")
    public ResponseEntity<ApiResponse<?>> updatePrice() {
        batchService.updatePriceHistory();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("시세 갱신 성공"));
    }
}
