package com.simulation.fifa.api.club.repository;

import com.simulation.fifa.api.club.dto.ClubListDto;
import com.simulation.fifa.api.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepositoryCustom {
    List<ClubListDto> findAllCustom();
}
