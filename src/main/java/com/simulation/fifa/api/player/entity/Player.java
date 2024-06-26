package com.simulation.fifa.api.player.entity;

import com.simulation.fifa.api.associations.PlayerClubAssociation;
import com.simulation.fifa.api.associations.PlayerPositionAssociation;
import com.simulation.fifa.api.associations.PlayerSkillAssociation;
import com.simulation.fifa.api.nation.entity.Nation;
import com.simulation.fifa.api.price.entity.PlayerPrice;
import com.simulation.fifa.api.season.entity.Season;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Player /*implements Persistable<Long>*/ {
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

    @Enumerated(EnumType.STRING)
    private PreferredFootEnum preferredFoot;

    private Integer leftFoot;

    private Integer rightFoot;

    private Integer pay;

    private Integer maxOverall;

    @ManyToOne
    Nation nation;

    @ManyToOne
    Season season;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    Set<PlayerPositionAssociation> playerPositionAssociations;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    Set<PlayerClubAssociation> playerClubAssociations;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    Set<PlayerSkillAssociation> playerSkillAssociations;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    Set<PlayerPrice> priceList;

    public void updateSeason(Season season) {
        this.season = season;
    }

    public void updateNation(Nation nation) {
        this.nation = nation;
    }

    public void updatePlayerPositionAssociations(Set<PlayerPositionAssociation> playerPositionAssociations) {
        this.playerPositionAssociations = playerPositionAssociations;
    }

    public void updatePlayerClubAssociations(Set<PlayerClubAssociation> playerClubAssociations) {
        this.playerClubAssociations = playerClubAssociations;
    }

    public void updatePlayerSkillAssociations(Set<PlayerSkillAssociation> playerSkillAssociations) {
        this.playerSkillAssociations = playerSkillAssociations;
    }

    public void updatePriceList(Set<PlayerPrice> priceList) {
        this.priceList = priceList;
    }

    /*******************************
     * bulk insert 성능 향상 위해 insert 전 select 안하도록 메소드 오버라이딩
     ******************************/

    /*@Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return true;
    }*/
}