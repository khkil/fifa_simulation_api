package com.simulation.fifa.api.club.entity;

import com.simulation.fifa.api.league.entity.League;
import jakarta.persistence.*;
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

    @Column(unique = true)
    private String clubName;

    @ManyToOne
    League league;
}
