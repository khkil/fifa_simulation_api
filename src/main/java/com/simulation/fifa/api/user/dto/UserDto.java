package com.simulation.fifa.api.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String ouid;
    private String nickname;
    private Integer level;
    private List<UserMatchTopRankDto.desc> topRanks;
}
