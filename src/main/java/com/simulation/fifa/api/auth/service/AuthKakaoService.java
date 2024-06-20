package com.simulation.fifa.api.auth.service;

import com.simulation.fifa.api.auth.dto.KakaoTokenResponseDto;
import com.simulation.fifa.api.auth.dto.KakaoUserResponseDto;
import com.simulation.fifa.api.user.entity.User;
import com.simulation.fifa.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthKakaoService {
    private final WebClient webClient;
    private final UserRepository userRepository;

    @Value("${oauth.kakao.rest-api-key}")
    private String restApiKey;
    @Value("${oauth.kakao.redirect-url}")
    private String redirectUrl;

    public void kakaoLogin(String code) {
        KakaoTokenResponseDto tokenInfo = getTokenInfo(code);

        String bearerToken = tokenInfo.getToken_type() + " " + tokenInfo.getAccess_token();

        KakaoUserResponseDto userInfo = getUserInfo(bearerToken);

        String kakaoAccountId = userInfo.getId();

        userRepository.findByAccountId(kakaoAccountId).ifPresentOrElse(user -> {

        }, () -> {

        });

    }

    private KakaoTokenResponseDto getTokenInfo(String code) {
        String grantType = "authorization_code";

        return webClient
                .mutate()
                .baseUrl("https://kauth.kakao.com")
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", grantType)
                        .queryParam("client_id", restApiKey)
                        .queryParam("redirect_uri", redirectUrl)
                        .queryParam("code", code)
                        .build()
                )
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }

    private KakaoUserResponseDto getUserInfo(String token) {
        return webClient
                .mutate()
                .baseUrl("https://kapi.kakao.com")
                .build()
                .post()
                .uri("/v2/user/me")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(KakaoUserResponseDto.class)
                .block();
    }
}
