package com.simulation.fifa.api.price.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.player.dto.QSquadDto_TotalPrice;
import com.simulation.fifa.api.player.dto.SquadDto;
import com.simulation.fifa.api.price.dto.PlayerRecentPriceDto;
import com.simulation.fifa.api.price.dto.QPlayerRecentPriceDto;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.max;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.price.entity.QPlayerPrice.playerPrice;

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
                        playerPrice.price,
                        playerPrice.grade
                ))
                .from(playerPrice)
                .join(playerPrice.player, player)
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
                .where(playerPrice.date.loe(previousDate))
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
}
