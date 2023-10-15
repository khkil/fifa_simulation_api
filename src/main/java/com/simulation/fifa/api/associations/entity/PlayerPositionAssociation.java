package com.simulation.fifa.api.associations.entity;

import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.position.entity.Position;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PlayerPositionAssociation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer overall;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Position position;
}
