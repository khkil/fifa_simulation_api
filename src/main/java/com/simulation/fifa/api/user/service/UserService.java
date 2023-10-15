package com.simulation.fifa.api.user.service;

import com.simulation.fifa.api.player.repository.PlayerRepository;
import com.simulation.fifa.api.user.dto.UserDto;
import com.simulation.fifa.api.user.dto.UserTradeListDto;
import com.simulation.fifa.api.user.dto.UserTradeRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {
    @Value("${nexon.fifa-online.public-api-url}")
    String publicApiUrl;
    @Value("${nexon.fifa-online.api-key}")
    String apiKey;

    @Autowired
    WebClient webClient;
    @Autowired
    PlayerRepository playerRepository;

    public List<UserTradeListDto> findAllTradeList(String nickname, UserTradeRequestDto userTradeRequestDto) {
        UserDto user = getUserInfo(nickname);

        return webClient
                .mutate()
                .baseUrl(publicApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/openapi/fconline/v1.0/users/" + user.getAccessId() + "/markets")
                        .queryParam("tradetype", userTradeRequestDto.getTradeType())
                        .queryParam("offset", userTradeRequestDto.getOffset())
                        .queryParam("limit", userTradeRequestDto.getLimit())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserTradeListDto>>() {
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }

    private UserDto getUserInfo(String nickname) {
        return webClient
                .mutate()
                .baseUrl(publicApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/openapi/fconline/v1.0/users")
                        .queryParam("nickname", nickname)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, apiKey)
                .retrieve()
                .bodyToMono(UserDto.class)
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }
}
