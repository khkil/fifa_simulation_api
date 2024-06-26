package com.simulation.fifa.api.user.dto;

import lombok.Data;

@Data
public class KakaoTokenResponseDto {
    private String access_token;
    private String token_type;
}
