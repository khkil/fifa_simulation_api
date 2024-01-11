package com.simulation.fifa.api.player.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.associations.entity.QPlayerClubAssociation;
import com.simulation.fifa.api.batch.dto.CheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.QCheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.QCheckPlayerPriceDto_Date;
import com.simulation.fifa.api.club.dto.QClubListDto;

import com.simulation.fifa.api.player.dto.*;
import com.simulation.fifa.api.position.dto.PositionDto;
import com.simulation.fifa.api.position.dto.QPositionDto;
import com.simulation.fifa.api.price.dto.QPlayerPriceListDto;

import com.simulation.fifa.api.season.dto.QSeasonListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.*;
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
                //season.id.ne(110L), // 교불 아이콘 제외
                seasonIdsIn(playerSearchDto.getSeasonIds()),
                //clubIdsAnd(playerSearchDto.getClubIds()),
                skillIdsIn(playerSearchDto.getSkillIds()),
                nameContains(playerSearchDto.getName()),
                nationIdsIn(playerSearchDto.getNationIds())
        };

        boolean hasWhere = !Arrays.stream(whereConditions).filter(Objects::nonNull).toList().isEmpty();

        List<Long> playerIds = jpaQueryFactory
                .select(player.id,
                        player.maxOverall,
                        player.name).distinct()
                .from(player)
                .leftJoin(player.playerPositionAssociations, playerPositionAssociation)
                .leftJoin(player.playerSkillAssociations, playerSkillAssociation)
                .leftJoin(playerSkillAssociation.skill, skill)
                .leftJoin(player.playerClubAssociations, playerClubAssociation)
                .leftJoin(playerClubAssociation.club, club)
                .leftJoin(player.season, season)
                .leftJoin(player.nation, nation)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(player.maxOverall.desc(), player.name.desc())
                .where(whereConditions)
                .fetch()
                .stream()
                .map(v -> v.get(player.id))
                .toList();

        if (playerIds.isEmpty()) {
            return new PageImpl<>(new ArrayList<>());
        }

        long count;

        if (!hasWhere) {
            count = jpaQueryFactory
                    .select(player.count())
                    .from(player)
                    .fetchFirst();
        } else {
            List<Long> searchedPlayerIds = jpaQueryFactory
                    .select(player.id).distinct()
                    .from(player)
                    .leftJoin(player.season, season)
                    .leftJoin(player.nation, nation)
                    .leftJoin(player.playerSkillAssociations, playerSkillAssociation)
                    .leftJoin(playerSkillAssociation.skill, skill)
                    .leftJoin(player.playerClubAssociations, playerClubAssociation)
                    .leftJoin(playerClubAssociation.club, club)
                    .where(whereConditions)
                    .fetch();

            count = jpaQueryFactory
                    .select(player.count())
                    .from(player)
                    .where(player.id.in(searchedPlayerIds))
                    .fetchFirst();
        }

        Map<Long, LocalDate> recentDateMap = jpaQueryFactory
                .selectFrom(playerPrice)
                .join(playerPrice.player, player)
                .where(player.id.in(playerIds))
                .transform(groupBy(player.id)
                        .as(max(playerPrice.date))
                );

        BooleanBuilder conditions = new BooleanBuilder();

        for (long playerId : playerIds) {
            if (recentDateMap.get(playerId) != null) {
                conditions.or(
                        player.id.eq(playerId)
                                .and(playerPrice.date.eq(recentDateMap.get(playerId)))
                );
            }
        }

        List<PlayerListDto> players = new ArrayList<>(jpaQueryFactory
                .selectFrom(player)
                .leftJoin(player.playerPositionAssociations, playerPositionAssociation)
                .leftJoin(playerPositionAssociation.position, position)
                .leftJoin(player.season, season)
                .leftJoin(player.priceList, playerPrice)
                .where(conditions)
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
                ).stream()
                .sorted(Comparator.comparingInt(a -> playerIds.indexOf(a.getSpId())))
                .toList()
        );

        return new PageImpl<>(players, pageable, count);
    }

    @Override
    public Optional<PlayerDetailDto> findByIdCustom(Long spId) {

        return jpaQueryFactory
                .selectFrom(player)
                .leftJoin(player.playerPositionAssociations, playerPositionAssociation)
                .leftJoin(playerPositionAssociation.position, position)
                .leftJoin(player.season, season)
                .leftJoin(player.priceList, playerPrice)
                .where(player.id.eq(spId))
                .transform(groupBy(player.id)
                        .list(new QPlayerDetailDto(
                                player.id,
                                player.name,
                                player.pay,
                                player.preferredFoot,
                                player.leftFoot,
                                player.rightFoot,
                                player.maxOverall,
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
                        )))
                .stream().findAny();
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

    private void clubIdsAnd(JPAQuery<Tuple> tuples, Long[] clubIds) {
        if (clubIds != null && clubIds.length > 0) {
            Arrays.stream(clubIds).forEach(v -> {
                QPlayerClubAssociation qPlayerClubAssociation = new QPlayerClubAssociation("player_club_" + v);
                tuples.join(player.playerClubAssociations, qPlayerClubAssociation).on(qPlayerClubAssociation.club.id.eq(v));
            });
        }
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


