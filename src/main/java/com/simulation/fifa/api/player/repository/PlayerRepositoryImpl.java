package com.simulation.fifa.api.player.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.club.dto.QClubListDto;
import com.simulation.fifa.api.player.dto.*;
import com.simulation.fifa.api.position.dto.QPositionDto;
import com.simulation.fifa.api.price.dto.QPlayerPriceListDto;

import com.simulation.fifa.api.season.dto.QSeasonListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.group.GroupBy.max;
import static com.querydsl.core.group.GroupBy.list;
import static com.simulation.fifa.api.association.entity.QPlayerClubAssociation.playerClubAssociation;
import static com.simulation.fifa.api.association.entity.QPlayerPositionAssociation.playerPositionAssociation;
import static com.simulation.fifa.api.association.entity.QPlayerSkillAssociation.playerSkillAssociation;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.position.entity.QPosition.position;
import static com.simulation.fifa.api.season.entity.QSeason.season;
import static com.simulation.fifa.api.club.entity.QClub.club;
import static com.simulation.fifa.api.price.entity.QPlayerPrice.playerPrice;
import static com.simulation.fifa.api.skill.entity.QSkill.skill;

@RequiredArgsConstructor
public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PlayerListDto> findAllCustom(Pageable pageable, PlayerSearchDto playerSearchDto) {
        Predicate[] whereConditions = new Predicate[]{
                clubIdsIn(playerSearchDto.getClubIds()),
                skillIdsIn(playerSearchDto.getSkillIds())
        };
        Set<Long> playerIds = new HashSet<>(
                jpaQueryFactory.select(player.id)
                        .from(player)
                        .join(player.playerSkillAssociations, playerSkillAssociation)
                        .join(playerSkillAssociation.skill, skill)
                        .join(player.playerClubAssociations, playerClubAssociation)
                        .join(playerClubAssociation.club, club)
                        .join(player.season, season)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .groupBy(player.id)
                        .where(whereConditions)
                        .fetch()
        );

        Long count = jpaQueryFactory
                .select(player.count())
                .from(player)
                .fetchOne();

        LocalDate maxDate = jpaQueryFactory
                .select(playerPrice.date.max())
                .from(playerPrice)
                .fetchOne();

        List<PlayerListDto> players = jpaQueryFactory
                .selectFrom(player)
                .join(player.playerPositionAssociations, playerPositionAssociation)
                .join(playerPositionAssociation.position, position)
                .join(player.season, season)
                .join(player.priceList, playerPrice)
                .where(player.id.in(playerIds), playerPrice.date.eq(maxDate))
                .orderBy(
                        playerPositionAssociation.overall.desc(),
                        player.id.desc(),
                        playerPrice.upgradeValue.asc()
                )
                .transform(groupBy(player.id)
                        .list(new QPlayerListDto(
                                player.id,
                                player.name,
                                player.pay,
                                player.preferredFoot,
                                player.leftFoot,
                                player.rightFoot,
                                set(new QPlayerPriceListDto(
                                        playerPrice.price,
                                        playerPrice.upgradeValue
                                )),
                                new QPlayerListDto_Average(
                                        speedAvg(),
                                        shootAvg(),
                                        passAvg(),
                                        dribbleAvg(),
                                        defendAvg(),
                                        physicalAvg()
                                ),
                                new QSeasonListDto(
                                        season.id,
                                        season.name,
                                        season.imageUrl
                                ),
                                set(new QPositionDto(
                                        position.positionName,
                                        playerPositionAssociation.overall
                                ))
                        ))
                );

        return new PageImpl<>(players, pageable, count);
    }

    @Override
    public Optional<PlayerDetailDto> findByIdCustom(Long id) {

        return jpaQueryFactory
                .selectFrom(player)
                .join(player.playerPositionAssociations, playerPositionAssociation)
                .join(playerPositionAssociation.position, position)
                .join(player.playerClubAssociations, playerClubAssociation)
                .join(playerClubAssociation.club, club)
                .join(player.season, season)
                .where(player.id.eq(id))
                .transform(groupBy(player.id)
                        .list(new QPlayerDetailDto(
                                player.id,
                                player.name,
                                new QSeasonListDto(
                                        season.id,
                                        season.name,
                                        season.imageUrl
                                ),
                                set(new QPositionDto(
                                        position.positionName,
                                        playerPositionAssociation.overall
                                )),
                                set(new QClubListDto(
                                        club.id,
                                        club.clubName
                                ))
                        ))
                ).stream().findAny();
    }

    private NumberExpression<Integer> speedAvg() {
        return (player.speed.add(player.acceleration)).divide(2);
    }

    private NumberExpression<Integer> shootAvg() {
        return (player.finishing
                .add(player.shootPower)
                .add(player.longShoot)
                .add(player.positioning)
                .add(player.volleyShoot)
                .add(player.penaltyKick)
        ).divide(6);
    }

    private NumberExpression<Integer> passAvg() {
        return (player.shortPass
                .add(player.vision)
                .add(player.crossing)
                .add(player.longPass)
                .add(player.freeKick)
                .add(player.curve)
        ).divide(6);
    }

    private NumberExpression<Integer> dribbleAvg() {
        return (player.dribble
                .add(player.ballControl)
                .add(player.agility)
                .add(player.balance)
                .add(player.reactionSpeed)
        ).divide(5);
    }

    private NumberExpression<Integer> defendAvg() {
        return (player.defending
                .add(player.tackling)
                .add(player.interception)
                .add(player.heading)
                .add(player.slideTackle)
        ).divide(5);
    }

    private NumberExpression<Integer> physicalAvg() {
        return (player.physicality
                .add(player.stamina)
                .add(player.determination)
                .add(player.jumping)
        ).divide(4);
    }

    private BooleanExpression clubIdsIn(Long[] clubIds) {
        return clubIds != null && clubIds.length > 0 ? club.id.in(clubIds) : null;
    }

    private BooleanExpression skillIdsIn(Long[] skillIds) {
        return skillIds != null && skillIds.length > 0 ? skill.id.in(skillIds) : null;
    }
}


