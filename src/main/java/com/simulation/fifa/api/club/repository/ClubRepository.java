package com.simulation.fifa.api.club.repository;

import com.simulation.fifa.api.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {
}
