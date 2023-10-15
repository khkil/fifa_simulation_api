package com.simulation.fifa.api.nation.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.nation.dto.NationListDto;

import static com.simulation.fifa.api.nation.entity.QNation.nation;

import com.simulation.fifa.api.nation.dto.QNationListDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NationRepositoryImpl implements NationRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<NationListDto> findAllCustom() {
        return jpaQueryFactory
                .select(new QNationListDto(
                        nation.id,
                        nation.nationName
                ))
                .from(nation)
                .fetch();
    }
}
