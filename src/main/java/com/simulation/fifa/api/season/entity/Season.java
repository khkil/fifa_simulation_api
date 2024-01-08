package com.simulation.fifa.api.season.entity;

import com.simulation.fifa.api.player.entity.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Season {
    @Id
    private Long id;

    private String name;

    private String imageUrl;

    private Boolean useSimulation;

    @OneToMany
    private List<Player> players;
}
