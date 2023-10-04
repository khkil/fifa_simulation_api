package com.simulation.fifa.api.player.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Player {
    @Id
    private Long spId;
}
