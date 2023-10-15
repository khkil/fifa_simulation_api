package com.simulation.fifa.api.nation.repository;

import com.simulation.fifa.api.nation.dto.NationListDto;

import java.util.List;

public interface NationRepositoryCustom {
    List<NationListDto> findAllCustom();
}
