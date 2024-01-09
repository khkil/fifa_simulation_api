package com.simulation.fifa.api.price.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.price.dto.PriceOverallDto;
import com.simulation.fifa.api.price.service.PlayerPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class PlayerPriceController {
    @Autowired
    PlayerPriceService playerPriceService;

    @GetMapping("/overall/{overall}")
    public ResponseEntity<ApiResponse<?>> findByOverall(@PathVariable Integer overall, Pageable pageable) {
        Page<PriceOverallDto> priceList = playerPriceService.findByOverall(overall, pageable);
        return ResponseEntity.ok(ApiResponse.createSuccess(priceList));
    }
}
