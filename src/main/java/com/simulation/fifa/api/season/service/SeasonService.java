package com.simulation.fifa.api.season.service;

import com.simulation.fifa.api.season.dto.SeasonListDto;
import com.simulation.fifa.api.season.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService {
    @Autowired
    SeasonRepository seasonRepository;

    public List<SeasonListDto> findAll() {
        return seasonRepository.findAllCustom();
    }
}
