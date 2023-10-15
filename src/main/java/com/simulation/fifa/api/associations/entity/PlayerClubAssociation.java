package com.simulation.fifa.api.associations.entity;

import com.simulation.fifa.api.club.entity.Club;
import com.simulation.fifa.api.player.entity.Player;
import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer startYear;

    private Integer endYear;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Club club;
}
