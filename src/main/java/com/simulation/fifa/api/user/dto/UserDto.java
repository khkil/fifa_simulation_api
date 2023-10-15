package com.simulation.fifa.api.user.dto;

import lombok.Data;

@Data
public class UserDto {
    private String accessId;
    private String nickname;
    private Integer level;
}
