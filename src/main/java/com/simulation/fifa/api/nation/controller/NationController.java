package com.simulation.fifa.api.nation.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.nation.dto.NationListDto;
import com.simulation.fifa.api.nation.service.NationService;
import com.simulation.fifa.api.player.PlayerService;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.dto.PlayerSearchDto;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/nations")
@RequiredArgsConstructor
public class NationController {
    private final NationService nationService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findAll(Pageable pageable, PlayerSearchDto playerSearchDto) {
        List<NationListDto> nations = nationService.findAll();
        return ResponseEntity.ok(ApiResponse.createSuccess(nations));
    }
}
