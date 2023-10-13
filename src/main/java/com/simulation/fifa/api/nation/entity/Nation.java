package com.simulation.fifa.api.nation.entity;

import com.simulation.fifa.api.player.entity.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Nation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String nationName;

    @OneToMany(mappedBy = "nation")
    private Set<Player> players;
}
