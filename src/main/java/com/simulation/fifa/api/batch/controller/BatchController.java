package com.simulation.fifa.api.batch.controller;

import com.simulation.fifa.api.batch.dto.CheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.PriceDateDto;
import com.simulation.fifa.api.batch.service.BatchService;
import com.simulation.fifa.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService batchService;

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

    @PostMapping("/price")
    public ResponseEntity<ApiResponse<?>> createPrice(@RequestBody PriceDateDto priceDateDto) {
        LocalDate date = priceDateDto.getDate();
        batchService.deletePreviousPrice();
        batchService.createPrice(date);
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("시세 생성 성공"));
    }

    @PostMapping("/daily-price")
    public ResponseEntity<ApiResponse<?>> createDailyPrice() {
        LocalDate localDate = LocalDate.now();
        batchService.deletePreviousPrice();
        batchService.createPrice(localDate);
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("오늘자 시세 생성 성공"));
    }

    @DeleteMapping("/previous-price")
    public ResponseEntity<ApiResponse<?>> deletePreviousPrice() {
        batchService.deletePreviousPrice();
        return ResponseEntity.ok(ApiResponse.createSuccessWithMessage("이전 시세 삭제 성공"));
    }

    @PostMapping("/check-price")
    public ResponseEntity<ApiResponse<?>> checkPrice() {
        List<CheckPlayerPriceDto> checkList = batchService.checkPrice();
        return ResponseEntity.ok(ApiResponse.createSuccess(checkList));
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
