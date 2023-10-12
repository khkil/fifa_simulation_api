package com.simulation.fifa.api.player.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.club.dto.QClubDto;
import com.simulation.fifa.api.player.dto.*;
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
import static com.querydsl.core.group.GroupBy.max;
import static com.querydsl.core.group.GroupBy.avg;
import static com.simulation.fifa.api.association.entity.QPlayerPositionAssociation.playerPositionAssociation;
import static com.simulation.fifa.api.association.entity.QPlayerClubAssociation.playerClubAssociation;
import static com.simulation.fifa.api.player.entity.QPlayer.player;
import static com.simulation.fifa.api.position.entity.QPosition.position;
import static com.simulation.fifa.api.season.entity.QSeason.season;
import static com.simulation.fifa.api.club.entity.QClub.club;
import static com.simulation.fifa.api.price.entity.QPlayerPrice.playerPrice;
import static com.querydsl.core.types.dsl.Expressions.asNumber;

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
                .join(player.priceList, playerPrice)
                .where(player.id.in(playerIds), playerPrice.date.eq(max(playerPrice.date)))
                .transform(groupBy(player.id)
                        .list(new QPlayerListDto(
                                player.id,
                                player.name,
                                player.pay,
                                player.preferredFoot,
                                player.leftFoot,
                                player.rightFoot,
                                playerPrice.price,
                                new QPlayerListDto_Average(
                                        speedAvg(),
                                        shootAvg(),
                                        passAvg(),
                                        dribbleAvg(),
                                        defendAvg(),
                                        physicalAvg()
                                ),
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

 /*
    스피드 o
    슛 o
    패스 o
    드리블
    수비
    피지컬*/
}


