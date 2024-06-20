package com.simulation.fifa.api.auth.dto;

import lombok.Data;

@Data
public class KakaoTokenResponseDto {
    private String access_token;
    private String token_type;
}
