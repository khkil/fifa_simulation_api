package com.simulation.fifa.api.user.service;

import com.simulation.fifa.api.user.dto.KakaoLoginRequestDto;
import com.simulation.fifa.api.user.dto.KakaoLoginResponseDto;
import com.simulation.fifa.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Qualifier("google")
@Component
@RequiredArgsConstructor
public class OAuth2Google2ServiceImpl implements OAuth2Service<KakaoLoginResponseDto, KakaoLoginRequestDto> {
    private final WebClient webClient;
    private final UserRepository userRepository;

    @Override
    public KakaoLoginResponseDto login(KakaoLoginRequestDto params) {
        return null;
    }

    @Override
    public void test() {
        System.out.println("구글");
    }

}
