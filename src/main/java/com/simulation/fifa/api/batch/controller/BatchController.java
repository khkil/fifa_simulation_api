package com.simulation.fifa.api.batch.controller;

import com.simulation.fifa.api.batch.service.BatchService;
import com.simulation.fifa.api.common.ApiResponse;
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

    @PostMapping("/base-informations")
    public ResponseEntity<ApiResponse<?>> createNations() {
        batchService.createLeagues();
        batchService.createClubs();
        batchService.createNations();
        batchService.createSeasons();
        batchService.createPositions();
        batchService.createSkills();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("기본 정보 생성 성공"));
    }

    @PostMapping("/players")
    public ResponseEntity<ApiResponse<?>> createPlayers() {
        batchService.createPlayers();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("선수 생성 성공"));
    }

    @PostMapping("/daily-price")
    public ResponseEntity<ApiResponse<?>> createDailyPrice() {
        batchService.createDailyPrice();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("오늘자 시세 생성 성공"));
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
}
