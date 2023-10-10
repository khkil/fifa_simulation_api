package com.simulation.fifa.api.player.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.dto.QPlayerListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.simulation.fifa.api.association.entity.QPlayerPositionAssociation.playerPositionAssociation;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.position.domain.QPosition.position;
import static com.simulation.fifa.api.season.entity.QSeason.season;

@RequiredArgsConstructor
public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PlayerListDto> findAllCustom(Pageable pageable) {
        Long count = jpaQueryFactory.select(player.count()).from(player).fetchOne();

        List<Long> playerIds = jpaQueryFactory
                .select(player.id)
                .from(player)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PlayerListDto> players = jpaQueryFactory
                .selectFrom(player)
                .join(player.playerPositionAssociations, playerPositionAssociation)
                .join(playerPositionAssociation.position, position)
                .join(player.season, season)
                .where(player.id.in(playerIds))
                .transform(groupBy(player.id)
                        .list(new QPlayerListDto(
                                player.id,
                                player.name,
                                set(position.positionName)
                        ))
                );

        return new PageImpl<>(players, pageable, count);
    }
}
