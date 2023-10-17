package com.simulation.fifa.api.player.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.batch.dto.CheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.QCheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.QCheckPlayerPriceDto_Date;
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
import static com.querydsl.core.group.GroupBy.list;
import static com.simulation.fifa.api.associations.entity.QPlayerClubAssociation.playerClubAssociation;
import static com.simulation.fifa.api.associations.entity.QPlayerPositionAssociation.playerPositionAssociation;
import static com.simulation.fifa.api.associations.entity.QPlayerSkillAssociation.playerSkillAssociation;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.position.entity.QPosition.position;
import static com.simulation.fifa.api.season.entity.QSeason.season;
import static com.simulation.fifa.api.club.entity.QClub.club;
import static com.simulation.fifa.api.price.entity.QPlayerPrice.playerPrice;
import static com.simulation.fifa.api.skill.entity.QSkill.skill;
import static com.simulation.fifa.api.nation.entity.QNation.nation;

@RequiredArgsConstructor
public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PlayerListDto> findAllCustom(Pageable pageable, PlayerSearchDto playerSearchDto) {
        Predicate[] whereConditions = new Predicate[]{
                seasonIdsIn(playerSearchDto.getSeasonIds()),
                clubIdsIn(playerSearchDto.getClubIds()),
                skillIdsIn(playerSearchDto.getSkillIds()),
                nameContains(playerSearchDto.getName()),
                nationIdsIn(playerSearchDto.getNationIds())
        };
        Set<Long> playerIds = new HashSet<>(
                jpaQueryFactory
                        .select(player.id)
                        .from(player)
                        .join(player.playerSkillAssociations, playerSkillAssociation)
                        .join(playerSkillAssociation.skill, skill)
                        .join(player.playerClubAssociations, playerClubAssociation)
                        .join(playerClubAssociation.club, club)
                        .join(player.season, season)
                        .join(player.nation, nation)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .groupBy(player.id)
                        .where(whereConditions)
                        .fetch()
        );

        int count = jpaQueryFactory
                .select(player.count())
                .from(player)
                .join(player.playerSkillAssociations, playerSkillAssociation)
                .join(playerSkillAssociation.skill, skill)
                .join(player.playerClubAssociations, playerClubAssociation)
                .join(playerClubAssociation.club, club)
                .join(player.season, season)
                .groupBy(player.id)
                .where(whereConditions)
                .fetch()
                .size();

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
                        playerPrice.grade.asc()
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
                                        playerPrice.grade
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

    @Override
    public List<CheckPlayerPriceDto> findCheckPrice() {
        return jpaQueryFactory
                .selectFrom(player)
                .join(player.priceList, playerPrice)
                .groupBy(playerPrice.date,
                        player.id
                )
                .transform(groupBy(player.id).list(
                        new QCheckPlayerPriceDto(
                                player.id,
                                list(new QCheckPlayerPriceDto_Date(
                                        playerPrice.date,
                                        playerPrice.date.count()
                                ))
                        )
                ));
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

    private BooleanExpression seasonIdsIn(Long[] seasonIds) {
        return seasonIds != null && seasonIds.length > 0 ? season.id.in(seasonIds) : null;
    }

    private BooleanExpression clubIdsIn(Long[] clubIds) {
        return clubIds != null && clubIds.length > 0 ? club.id.in(clubIds) : null;
    }

    private BooleanExpression skillIdsIn(Long[] skillIds) {
        return skillIds != null && skillIds.length > 0 ? skill.id.in(skillIds) : null;
    }

    private BooleanExpression nationIdsIn(Long[] nationIds) {
        return nationIds != null && nationIds.length > 0 ? nation.id.in(nationIds) : null;
    }

    private BooleanExpression nameContains(String name) {
        return name != null && !name.isEmpty() ? player.name.contains(name) : null;
    }
}


