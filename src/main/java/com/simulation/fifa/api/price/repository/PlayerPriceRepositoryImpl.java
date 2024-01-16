package com.simulation.fifa.api.price.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.position.dto.QPositionDto;
import com.simulation.fifa.api.price.dto.*;
import com.simulation.fifa.api.price.entity.QPlayerPrice;
import com.simulation.fifa.api.season.dto.QSeasonListDto;
import com.simulation.fifa.api.season.entity.Season;
import com.simulation.fifa.api.user.dto.squad.QSquadDto_TotalPrice;
import com.simulation.fifa.api.user.dto.squad.SquadDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.*;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.price.entity.QPlayerPrice.playerPrice;
import static com.simulation.fifa.api.season.entity.QSeason.season;
import static com.simulation.fifa.api.position.entity.QPosition.position;
import static com.simulation.fifa.api.associations.entity.QPlayerPositionAssociation.playerPositionAssociation;

@RequiredArgsConstructor
public class PlayerPriceRepositoryImpl implements PlayerPriceRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PlayerRecentPriceDto> findRecentPriceList(List<Long> playerIds, List<Integer> grades) {
        Map<Long, LocalDate> recentDatemap = jpaQueryFactory
                .selectFrom(playerPrice)
                .join(playerPrice.player, player)
                .where(player.id.in(playerIds))
                .transform(groupBy(player.id)
                        .as(max(playerPrice.date))
                );

        BooleanBuilder conditions = new BooleanBuilder();

        for (int i = 0; i < playerIds.size(); i++) {
            Long playerId = playerIds.get(i);
            if (recentDatemap.get(playerId) != null) {
                conditions.or(
                        player.id.eq(playerId)
                                .and(playerPrice.grade.eq(grades.get(i)))
                                .and(playerPrice.date.eq(recentDatemap.get(playerId)))
                );
            }
        }

        return jpaQueryFactory
                .select(new QPlayerRecentPriceDto(
                        player.id,
                        player.name,
                        season.id,
                        season.imageUrl,
                        playerPrice.price,
                        playerPrice.grade
                ))
                .from(playerPrice)
                .join(playerPrice.player, player)
                .join(player.season, season)
                .where(conditions)
                .fetch();
    }

    @Override
    public List<Long> findByNotRenewalPrice(LocalDate localDate) {
        List<Long> ids = jpaQueryFactory
                .select(playerPrice.player.id)
                .from(playerPrice)
                .join(playerPrice.player, player)
                .where(playerPrice.date.eq(localDate))
                .groupBy(player.id)
                .fetch();

        return jpaQueryFactory
                .select(player.id)
                .from(player)
                .where(player.id.notIn(ids))
                .fetch();
    }

    @Override
    public long deletePreviousPrice(LocalDate previousDate) {
        return jpaQueryFactory
                .delete(playerPrice)
                .where(playerPrice.date.eq(previousDate))
                .execute();
    }

    @Override
    public List<SquadDto.TotalPrice> findPlayerPriceByIdsAndDateBetween(List<SquadDto.Player> players, LocalDate start, LocalDate end) {
        BooleanBuilder playerGradeConditions = new BooleanBuilder();
        players.forEach(p -> playerGradeConditions.or(
                player.id.eq(p.getSpid()).and(playerPrice.grade.eq(p.getBuildUp()))
        ));

        return jpaQueryFactory
                .select(new QSquadDto_TotalPrice(
                        playerPrice.price.sum(),
                        playerPrice.date
                ))
                .from(playerPrice)
                .join(playerPrice.player, player)
                .where(playerPrice.date.between(start, end), playerGradeConditions)
                .groupBy(playerPrice.date)
                .orderBy(playerPrice.date.desc())
                .fetch();

    }

    @Override
    public List<PlayerPriceWaveDto> findPriceRanks(Pageable pageable) {
        List<LocalDate> dateList = jpaQueryFactory
                .select(playerPrice.date)
                .from(playerPrice)
                .join(playerPrice.player, player)
                .where(player.id.eq(100000051L), playerPrice.grade.eq(1))
                .offset(0)
                .limit(2)
                .orderBy(playerPrice.date.desc())
                .fetch();

        LocalDate yesterday = dateList.get(1);
        LocalDate today = dateList.get(0);

        Sort sort = pageable.getSort();
        Sort.Order order = sort.getOrderFor("wave");

        QPlayerPrice todayPrice = new QPlayerPrice("today");
        QPlayerPrice yesterdayPrice = new QPlayerPrice("yesterday");

        NumberExpression<Long> percentage = (todayPrice.price.subtract(yesterdayPrice.price)).divide(yesterdayPrice.price).multiply(100);

        return jpaQueryFactory.select(new QPlayerPriceWaveDto(
                        player.id,
                        player.name,
                        yesterdayPrice.price,
                        todayPrice.price,
                        percentage,
                        season.imageUrl
                ))
                .from(player)
                .join(player.priceList, todayPrice)
                .join(yesterdayPrice).on(todayPrice.player.id.eq(yesterdayPrice.player.id))
                .join(player.season, season)
                .where(todayPrice.grade.eq(1),
                        yesterdayPrice.grade.eq(1),
                        todayPrice.date.eq(today),
                        yesterdayPrice.date.eq(yesterday),
                        yesterdayPrice.price.goe(1000),
                        todayPrice.price.goe(1000),
                        season.id.notIn(300L)
                )
                .orderBy(order != null && order.isAscending()
                        ? percentage.asc()
                        : percentage.desc()
                )
                .limit(10)
                .fetch();
    }

    @Override
    public Page<PriceOverallDto> findByOverall(Integer overall, Pageable pageable) {
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


        List<PriceOverallDto> list = jpaQueryFactory
                .selectFrom(playerPrice)
                .leftJoin(playerPrice.player, player)
                .leftJoin(player.season, season)
                .leftJoin(player.playerPositionAssociations, playerPositionAssociation)
                .leftJoin(playerPositionAssociation.position, position)
                .where(playerPrice.id.in(playerIds))
                .orderBy(playerPrice.price.asc(), player.id.desc())
                .transform(groupBy(player.id).list(
                        new QPriceOverallDto(
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
