package com.simulation.fifa.api.position.entity;

import com.simulation.fifa.api.association.entity.PlayerPositionAssociation;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Position {
    @Id
    Long id;

    @OneToMany(mappedBy = "position")
    List<PlayerPositionAssociation> playerPositionAssociations;

    private String positionName;
}
