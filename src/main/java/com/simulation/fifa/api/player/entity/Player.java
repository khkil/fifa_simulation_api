package com.simulation.fifa.api.player.entity;

import com.simulation.fifa.api.association.entity.PlayerPositionAssociation;
import com.simulation.fifa.api.season.entity.Season;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Player implements Persistable<Long> {
    @Id
    private Long id;

    private String name;

    private Integer speed;

    private Integer acceleration;

    private Integer finishing;

    private Integer shootPower;

    private Integer longShoot;

    private Integer positioning;

    private Integer volleyShoot;

    private Integer penaltyKick;

    private Integer shortPass;

    private Integer vision;

    private Integer crossing;

    private Integer longPass;

    private Integer freeKick;

    private Integer curve;

    private Integer dribble;

    private Integer ballControl;

    private Integer agility;

    private Integer balance;

    private Integer reactionSpeed;

    private Integer defending;

    private Integer tackling;

    private Integer interception;

    private Integer heading;

    private Integer slideTackle;

    private Integer physicality;

    private Integer stamina;

    private Integer determination;

    private Integer jumping;

    private Integer composure;

    private Integer gkDiving;

    private Integer gkHandling;

    private Integer gkKicking;

    private Integer gkReflexes;

    private Integer gkPositioning;

    @ManyToOne
    Season season;

    @OneToMany(mappedBy = "player")
    Set<PlayerPositionAssociation> playerPositionAssociations;

    public void updateSeason(Season season) {
        this.season = season;
    }

    /*******************************
     * bulk insert 성능 향상 위해 insert 전 select 안하도록 메소드 오버라이딩
     ******************************/

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}