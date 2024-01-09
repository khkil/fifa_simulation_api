package com.simulation.fifa.api.price.repository;

import com.simulation.fifa.api.price.dto.PriceOverallDto;
import com.simulation.fifa.api.price.entity.PlayerPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerPriceRepository extends JpaRepository<PlayerPrice, Long>, PlayerPriceRepositoryCustom {
}
