package com.simulation.fifa.api.nation.service;

import com.simulation.fifa.api.nation.dto.NationListDto;
import com.simulation.fifa.api.nation.entity.Nation;
import com.simulation.fifa.api.nation.repository.NationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NationService {
    private final NationRepository nationRepository;

    public List<NationListDto> findAll() {
        return nationRepository.findAllCustom();
    }
}
