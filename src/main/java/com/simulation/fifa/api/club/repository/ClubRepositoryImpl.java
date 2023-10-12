package com.simulation.fifa.api.club.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.simulation.fifa.api.club.dto.ClubListDto;

import static com.simulation.fifa.api.club.entity.QClub.club;

import com.simulation.fifa.api.club.dto.QClubListDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ClubRepositoryImpl implements ClubRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ClubListDto> findAllCustom() {

        return jpaQueryFactory
                .select(new QClubListDto(
                        club.id,
                        club.clubName
                ))
                .from(club)
                .orderBy(club.id.asc())
                .fetch();
    }
}
