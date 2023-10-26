package com.simulation.fifa.api.club.service;

import com.simulation.fifa.api.club.dto.ClubListDto;
import com.simulation.fifa.api.club.repository.ClubRepository;
import com.simulation.fifa.api.skill.dto.SkillListDto;
import com.simulation.fifa.api.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;

    public List<ClubListDto> findAll() {
        return clubRepository.findAllCustom();
    }
}
