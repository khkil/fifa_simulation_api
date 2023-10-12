package com.simulation.fifa.api.skill.service;

import com.simulation.fifa.api.skill.dto.SkillListDto;
import com.simulation.fifa.api.skill.dto.SkillSearchDto;
import com.simulation.fifa.api.skill.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {
    @Autowired
    SkillRepository skillRepository;

    public List<SkillListDto> findAll() {
        return skillRepository.findAllCustom();
    }
}
