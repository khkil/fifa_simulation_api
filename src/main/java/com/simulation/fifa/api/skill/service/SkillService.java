package com.simulation.fifa.api.skill.service;

import com.simulation.fifa.api.skill.dto.SkillListDto;
import com.simulation.fifa.api.skill.dto.SkillSearchDto;
import com.simulation.fifa.api.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public List<SkillListDto> findAll() {
        return skillRepository.findAllCustom();
    }
}
