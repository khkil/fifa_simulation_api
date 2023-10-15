package com.simulation.fifa.api.user.service;

import com.simulation.fifa.api.batch.dto.SpIdDto;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import com.simulation.fifa.api.season.dto.SeasonListDto;
import com.simulation.fifa.api.season.entity.Season;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Value("${nexon.fifa-online.public-api-url}")
    private String publicApiUrl;
    @Value("${nexon.fifa-online.api-key}")
    private String apiKey;

    @Autowired
    WebClient webClient;
    @Autowired
    PlayerRepository playerRepository;

    public List<UserTradeListDto> findAllTradeList(String nickname, UserTradeRequestDto userTradeRequestDto) {
        UserDto user = getUserInfo(nickname);

        List<UserTradeListDto> userTradeList = webClient
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

        Set<Long> spIdList = userTradeList.stream().map(UserTradeListDto::getSpid).collect(Collectors.toSet());
        List<Player> players = playerRepository.findAllByIdIn(spIdList);

        for (Player player : players) {
            for (UserTradeListDto trade : userTradeList) {
                if (trade.getSpid().equals(player.getId())) {
                    Season season = player.getSeason();
                    
                    trade.setPlayerName(player.getName());
                    trade.setSeason(new SeasonListDto(
                            season.getId(),
                            season.getName(),
                            season.getImageUrl()
                    ));
                }
            }
        }

        return userTradeList;
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
