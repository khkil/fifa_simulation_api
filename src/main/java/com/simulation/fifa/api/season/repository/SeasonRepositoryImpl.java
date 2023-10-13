package com.simulation.fifa.api.season.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.season.dto.QSeasonListDto;
import com.simulation.fifa.api.season.dto.SeasonListDto;

import static com.simulation.fifa.api.season.entity.QSeason.season;

import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class SeasonRepositoryImpl implements SeasonRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SeasonListDto> findAllCustom() {
        return jpaQueryFactory.select(
                        new QSeasonListDto(
                                season.id,
                                season.name,
                                season.imageUrl
                        ))
                .from(season)
                .fetch();
    }
}
