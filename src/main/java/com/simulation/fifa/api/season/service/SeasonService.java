package com.simulation.fifa.api.season.service;

import com.simulation.fifa.api.season.dto.SeasonListDto;
import com.simulation.fifa.api.season.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonService {
    private final SeasonRepository seasonRepository;

    public List<SeasonListDto> findAll() {
        return seasonRepository.findAllCustom();
    }
}
