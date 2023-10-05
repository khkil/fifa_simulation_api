package com.simulation.fifa.api.player.entity;

import com.simulation.fifa.api.position.domain.Position;
import com.simulation.fifa.api.season.entity.Season;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    private Long id;

    private String name;

    private String abilities;

    private int speed;

    private int acceleration;

    private int finishing;

    private int shootPower;

    private int longShoot;

    private int positioning;

    private int volleyShoot;

    private int penaltyKick;

    private int shortPass;

    private int vision;

    private int crossing;

    private int longPass;

    private int freeKick;

    private int curve;

    private int dribble;

    private int ballControl;

    private int agility;

    private int balance;

    private int reactionSpeed;

    private int defending;

    private int tackling;

    private int interception;

    private int heading;

    private int slideTackle;

    private int physicality;

    private int stamina;

    private int determination;

    private int jumping;

    private int composure;

    private int gkDiving;

    private int gkHandling;

    private int gkKicking;

    private int gkReflexes;

    private int gkPositioning;

    @ManyToOne
    Season season;

    public void updateSeason(Season season) {
        this.season = season;
    }
}