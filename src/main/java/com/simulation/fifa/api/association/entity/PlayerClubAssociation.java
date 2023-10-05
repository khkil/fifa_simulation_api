package com.simulation.fifa.api.association.entity;

import com.simulation.fifa.api.club.entity.Club;
import com.simulation.fifa.api.player.entity.Player;
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
@Getter
@Builder
public class PlayerClubAssociation {
    @Id
    Long id;

    @ManyToOne
    Player player;

    @ManyToOne
    Club club;
}
