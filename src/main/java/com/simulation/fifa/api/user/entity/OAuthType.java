package com.simulation.fifa.api.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OAuthType {
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");

    private String type;
}
