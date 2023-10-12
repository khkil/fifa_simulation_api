package com.simulation.fifa.api.player.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.club.dto.QClubDto;
import com.simulation.fifa.api.player.dto.PlayerDetailDto;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.dto.QPlayerDetailDto;
import com.simulation.fifa.api.player.dto.QPlayerListDto;
import com.simulation.fifa.api.position.dto.QPositionDto;
import com.simulation.fifa.api.season.dto.QSeasonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.simulation.fifa.api.association.entity.QPlayerPositionAssociation.playerPositionAssociation;
import static com.simulation.fifa.api.association.entity.QPlayerClubAssociation.playerClubAssociation;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.position.entity.QPosition.position;
import static com.simulation.fifa.api.season.entity.QSeason.season;
import static com.simulation.fifa.api.club.entity.QClub.club;

@RequiredArgsConstructor
public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PlayerListDto> findAllCustom(Pageable pageable) {
        List<Long> playerIds = jpaQueryFactory
                .select(player.id)
                .from(player)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = jpaQueryFactory
                .select(player.count())
                .from(player)
                .fetchOne();

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
                                player.pay,
                                player.preferredFoot,
                                player.leftFoot,
                                player.rightFoot,
                                new QSeasonDto(
                                        season.id,
                                        season.name,
                                        season.imageUrl
                                ),
                                set(new QPositionDto(
                                        position.positionName,
                                        playerPositionAssociation.stat
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
                                new QSeasonDto(
                                        season.id,
                                        season.name,
                                        season.imageUrl
                                ),
                                set(new QPositionDto(
                                        position.positionName,
                                        playerPositionAssociation.stat
                                )),
                                set(new QClubDto(
                                        club.id,
                                        club.clubName
                                ))
                        ))
                ).stream().findAny();
    }
}
