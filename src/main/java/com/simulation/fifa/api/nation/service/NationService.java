package com.simulation.fifa.api.nation.service;

import com.simulation.fifa.api.nation.entity.Nation;
import com.simulation.fifa.api.nation.repository.NationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NationService {
    @Autowired
    NationRepository nationRepository;
}
