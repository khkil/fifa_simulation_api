package com.simulation.fifa.api.price.repository;

import com.simulation.fifa.api.price.entity.PlayerPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerPriceRepository extends JpaRepository<PlayerPrice, Long>, PlayerPriceRepositoryCustom {
}
