package com.simulation.fifa.api.auth.dto;

import lombok.Data;

@Data
public class KakaoLoginResponseDto {
    private String id;
    private String connected_at;
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {

        private String email;
        private Profile profile;


        @Data
        private static class Profile {
            private String nickname;
        }
    }
}
