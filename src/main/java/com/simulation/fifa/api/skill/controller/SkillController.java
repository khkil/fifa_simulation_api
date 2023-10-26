package com.simulation.fifa.api.skill.controller;

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
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> findALl() {
        List<SkillListDto> skills = skillService.findAll();
        return ResponseEntity.ok(ApiResponse.createSuccess(skills));
    }
}
