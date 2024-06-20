package com.simulation.fifa.api.auth.dto;

import lombok.Data;

@Data
public class KakaoUserResponseDto {
    private String id;
    private String connected_at;
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {

        private String name;
        private String email;
        private String phone_number;
    }
}
