package com.simulation.fifa.api.season.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.season.dto.SeasonListDto;
import com.simulation.fifa.api.season.service.SeasonService;
import com.simulation.fifa.api.skill.dto.SkillListDto;
import com.simulation.fifa.api.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
public class SeasonController {
    private final SeasonService seasonService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findALl() {
        List<SeasonListDto> seasons = seasonService.findAll();
        return ResponseEntity.ok(ApiResponse.createSuccess(seasons));
    }
}
