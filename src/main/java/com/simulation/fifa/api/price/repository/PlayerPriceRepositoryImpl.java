package com.simulation.fifa.api.price.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.simulation.fifa.api.price.dto.PlayerRecentPriceDto;

import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.price.entity.QPlayerPrice.playerPrice;

import com.simulation.fifa.api.price.dto.QPlayerRecentPriceDto;
import com.simulation.fifa.api.price.dto.QPlayerRecentPriceDto_PlayerRecentDate;
import lombok.RequiredArgsConstructor;

import java.beans.Expression;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class PlayerPriceRepositoryImpl implements PlayerPriceRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PlayerRecentPriceDto> findRecentPriceList(List<Long> playerIds, List<Integer> grades) {
        LocalDate recentDate = jpaQueryFactory
                .select(playerPrice.date.max())
                .from(playerPrice)
                .fetchOne();

        BooleanBuilder conditions = new BooleanBuilder();
        for (int i = 0; i < playerIds.size(); i++) {
            Long playerId = playerIds.get(i);
            conditions.or(
                    player.id.eq(playerId)
                            .and(playerPrice.grade.eq(grades.get(i)))
                            .and(playerPrice.date.eq(recentDate))
            );
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
    public List<Long> findByNotRenewalPrice() {
        return jpaQueryFactory
                .select(playerPrice.player.id)
                .from(playerPrice)
                .join(playerPrice.player, player)
                .groupBy(player.id)
                .having(playerPrice.date.max().ne(LocalDate.now()))
                .fetch();
    }
}
