package com.simulation.fifa.api.user.exception;

import lombok.Getter;

@Getter
public class NicknameNotFoundException extends RuntimeException {
    final static String errorMessage = "해당 닉네임을 가진 유저를 찾을수 없습니다.";

    public NicknameNotFoundException(Throwable cause) {
        super(errorMessage, cause);
    }

    public NicknameNotFoundException() {
        super(errorMessage);
    }
}
