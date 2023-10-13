package com.simulation.fifa.api.season.repository;

import com.simulation.fifa.api.season.dto.SeasonListDto;
import com.simulation.fifa.api.season.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeasonRepositoryCustom {
    List<SeasonListDto> findAllCustom();
}
