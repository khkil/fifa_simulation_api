package com.simulation.fifa.api.player.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.associations.QPlayerClubAssociation;
import com.simulation.fifa.api.batch.dto.CheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.QCheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.QCheckPlayerPriceDto_Date;
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

import static com.querydsl.core.group.GroupBy.*;
import static com.simulation.fifa.api.associations.QPlayerClubAssociation.playerClubAssociation;
import static com.simulation.fifa.api.associations.QPlayerPositionAssociation.playerPositionAssociation;
import static com.simulation.fifa.api.associations.QPlayerSkillAssociation.playerSkillAssociation;
import static com.simulation.fifa.api.club.entity.QClub.club;
import static com.simulation.fifa.api.nation.entity.QNation.nation;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.position.entity.QPosition.position;
import static com.simulation.fifa.api.price.entity.QPlayerPrice.playerPrice;
import static com.simulation.fifa.api.season.entity.QSeason.season;
import static com.simulation.fifa.api.skill.entity.QSkill.skill;

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

    @Override
    public Page<PlayerByOverallDto> findByOverall(Integer overall, Pageable pageable) {
        LocalDate recentDate = jpaQueryFactory
                .select(playerPrice.date)
                .from(playerPrice)
                .orderBy(playerPrice.date.desc())
                .fetchFirst();

        List<Long> usedSeasonIds = jpaQueryFactory.select(season.id).from(season).where(season.useSimulation.eq(true)).fetch();

        Predicate[] whereConditions = new Predicate[]{
                playerPrice.grade.loe(7), // 강화 재료 표시 > 1~7강 까지만
                calculateOverallByGrade(player.maxOverall).eq(overall),
                playerPrice.date.eq(recentDate),
                season.id.in(usedSeasonIds)
        };

        List<Long> playerIds = jpaQueryFactory
                .select(playerPrice.id)
                .from(playerPrice)
                .leftJoin(playerPrice.player, player)
                .leftJoin(player.season, season)
                .where(whereConditions)
                .orderBy(playerPrice.price.asc(), player.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        List<PlayerByOverallDto> list = jpaQueryFactory
                .selectFrom(playerPrice)
                .leftJoin(playerPrice.player, player)
                .leftJoin(player.season, season)
                .leftJoin(player.playerPositionAssociations, playerPositionAssociation)
                .leftJoin(playerPositionAssociation.position, position)
                .where(playerPrice.id.in(playerIds))
                .orderBy(playerPrice.price.asc(), player.id.desc())
                .transform(groupBy(player.id).list(
                        new QPlayerByOverallDto(
                                player.id,
                                player.name,
                                calculateOverallByGrade(player.maxOverall),
                                playerPrice.price,
                                playerPrice.grade,
                                new QSeasonListDto(
                                        season.id,
                                        season.name,
                                        season.imageUrl
                                ),
                                set(new QPositionDto(position.positionName, calculateOverallByGrade(playerPositionAssociation.overall)))
                        )
                ));

        Long count = jpaQueryFactory
                .select(playerPrice.count())
                .from(playerPrice)
                .leftJoin(playerPrice.player, player)
                .leftJoin(player.season, season)
                .where(whereConditions)
                .fetchOne();

        return new PageImpl<>(list, pageable, count);
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

    private NumberExpression<Integer> calculateOverallByGrade(NumberExpression<Integer> overall) {
        NumberExpression<Integer> plusStat = new CaseBuilder()
                .when(playerPrice.grade.eq(2)).then(1)
                .when(playerPrice.grade.eq(3)).then(2)
                .when(playerPrice.grade.eq(4)).then(4)
                .when(playerPrice.grade.eq(5)).then(6)
                .when(playerPrice.grade.eq(6)).then(8)
                .when(playerPrice.grade.eq(7)).then(11)
                .when(playerPrice.grade.eq(8)).then(15)
                .when(playerPrice.grade.eq(9)).then(19)
                .when(playerPrice.grade.eq(10)).then(24)
                .otherwise(0);
        return overall.add(plusStat);
    }
}


