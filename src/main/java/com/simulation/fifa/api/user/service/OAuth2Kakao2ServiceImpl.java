package com.simulation.fifa.api.user.service;

import com.simulation.fifa.api.user.dto.KakaoTokenResponseDto;
import com.simulation.fifa.api.user.dto.KakaoLoginRequestDto;
import com.simulation.fifa.api.user.dto.KakaoLoginResponseDto;
import com.simulation.fifa.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Qualifier("kakao")
@Component
@RequiredArgsConstructor
public class OAuth2Kakao2ServiceImpl implements OAuth2Service<KakaoLoginResponseDto, KakaoLoginRequestDto> {
    private final WebClient webClient;
    private final UserRepository userRepository;

    @Value("${oauth.kakao.rest-api-key}")
    private String restApiKey;
    @Value("${oauth.kakao.redirect-url}")
    private String redirectUrl;

    @Override
    public KakaoLoginResponseDto login(KakaoLoginRequestDto params) {
        KakaoTokenResponseDto tokenInfo = getTokenInfo(params.getCode());

        String bearerToken = tokenInfo.getToken_type() + " " + tokenInfo.getAccess_token();
        KakaoLoginResponseDto userInfo = getUserInfo(bearerToken);

        String kakaoAccountId = userInfo.getId();

        userRepository.findByAccountId(kakaoAccountId).ifPresentOrElse(user -> {

        }, () -> {

        });
        return null;
    }

    @Override
    public void test() {
        System.out.println("카카오");
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

    private KakaoLoginResponseDto getUserInfo(String token) {
        return webClient
                .mutate()
                .baseUrl("https://kapi.kakao.com")
                .build()
                .post()
                .uri("/v2/user/me")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(KakaoLoginResponseDto.class)
                .block();
    }
}
