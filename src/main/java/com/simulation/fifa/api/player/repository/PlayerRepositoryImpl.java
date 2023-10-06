package com.simulation.fifa.api.player.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.player.dto.PlayerListDto;

import static com.simulation.fifa.api.player.entity.QPlayer.player;

import com.simulation.fifa.api.player.dto.QPlayerListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PlayerRepositoryImpl implements PlayerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PlayerListDto> findAllCustom(Pageable pageable) {
        Long count = jpaQueryFactory.select(player.count()).from(player).fetchOne();
        List<PlayerListDto> players = jpaQueryFactory
                .select(new QPlayerListDto(
                        player.id,
                        player.name
                ))
                .from(player)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(players, pageable, count);
    }
}
