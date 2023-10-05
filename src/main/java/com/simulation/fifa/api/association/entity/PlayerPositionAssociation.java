package com.simulation.fifa.api.association.entity;

import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.position.domain.Position;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PlayerPositionAssociation {
    @Id
    Long id;

    @ManyToOne
    Player player;

    @ManyToOne
    Position position;
}
