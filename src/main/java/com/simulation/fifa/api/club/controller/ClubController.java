package com.simulation.fifa.api.club.controller;

import com.simulation.fifa.api.club.dto.ClubListDto;
import com.simulation.fifa.api.club.service.ClubService;
import com.simulation.fifa.api.common.ApiResponse;
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
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findALl() {
        List<ClubListDto> clubs = clubService.findAll();
        return ResponseEntity.ok(ApiResponse.createSuccess(clubs));
    }
}
