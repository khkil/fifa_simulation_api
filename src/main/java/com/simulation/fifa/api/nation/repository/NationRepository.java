package com.simulation.fifa.api.nation.repository;

import com.simulation.fifa.api.nation.dto.NationListDto;
import com.simulation.fifa.api.nation.entity.Nation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NationRepository extends JpaRepository<Nation, Long>, NationRepositoryCustom {
}
