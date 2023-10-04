package com.simulation.fifa.api.club.entity;

import com.simulation.fifa.api.league.entity.League;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club {
    @Id
    private Long id;

    private String clubName;

    @ManyToOne
    League league;
}
