package com.simulation.fifa.api.user.service;

import com.simulation.fifa.api.batch.service.BatchService;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import com.simulation.fifa.api.price.dto.PlayerRecentPriceDto;
import com.simulation.fifa.api.price.repository.PlayerPriceRepository;
import com.simulation.fifa.api.season.dto.SeasonListDto;
import com.simulation.fifa.api.season.entity.Season;
import com.simulation.fifa.api.user.dto.UserDto;
import com.simulation.fifa.api.user.dto.UserTradeListDto;
import com.simulation.fifa.api.user.dto.UserTradeRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserService {
    @Value("${nexon.fifa-online.public-api-url}")
    private String publicApiUrl;
    @Value("${nexon.fifa-online.api-key}")
    private String apiKey;

    @Autowired
    WebClient webClient;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    PlayerPriceRepository playerPriceRepository;
    @Autowired
    BatchService batchService;

    public List<UserTradeListDto> findAllTradeList(String nickname, UserTradeRequestDto userTradeRequestDto) {
        UserDto user = getUserInfo(nickname);

        List<UserTradeListDto> buyList = getUserTradeList(user.getAccessId(), UserTradeRequestDto
                .builder()
                .tradeType("buy")
                .offset(userTradeRequestDto.getOffset())
                .limit(userTradeRequestDto.getLimit())
                .build()
        );

        List<UserTradeListDto> sellList = getUserTradeList(user.getAccessId(), UserTradeRequestDto
                .builder()
                .tradeType("sell")
                .offset(userTradeRequestDto.getOffset())
                .limit(userTradeRequestDto.getLimit())
                .build()
        );

        List<UserTradeListDto> joinedList = Stream
                .concat(buyList.stream(), sellList.stream())
                .sorted((a, b) -> b.getTradeDate().compareTo(a.getTradeDate()))
                .toList();

        List<Long> spIdList = joinedList.stream().map(UserTradeListDto::getSpid).toList();
        List<Integer> gradeList = joinedList.stream().map(UserTradeListDto::getGrade).toList();

        List<Player> players = playerRepository.findAllByIdIn(spIdList);
        List<PlayerRecentPriceDto> priceList = playerPriceRepository.findRecentPriceList(spIdList, gradeList);

        // 유저의 거래 내역 조희중 크롤링 미 완료된 데이터 생성
        if (new HashSet<>(players).size() < new HashSet<>(spIdList).size()) {
            Set<Long> playerIdSet = new HashSet<>(players).stream().map(Player::getId).collect(Collectors.toSet());
            Set<Long> missedPlayers = new HashSet<>(spIdList).stream().filter(v -> !playerIdSet.contains(v)).collect(Collectors.toSet());
            batchService.createPlayers(missedPlayers);
        }

        for (Player player : players) {
            for (UserTradeListDto trade : joinedList) {
                if (trade.getSpid().equals(player.getId())) {
                    Season season = player.getSeason();
                    priceList.stream()
                            .filter(v -> v.getGrade().equals(trade.getGrade())
                                    && v.getPlayerId().equals(trade.getSpid())
                            )
                            .findFirst()
                            .ifPresent(
                                    playerRecentPriceDto -> trade.setRecentPrice(playerRecentPriceDto.getPrice())
                            );

                    trade.setPlayerName(player.getName());
                    trade.setSeason(new SeasonListDto(
                            season.getId(),
                            season.getName(),
                            season.getImageUrl()
                    ));
                }
            }
        }

        return joinedList;
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
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("닉네임 : " + nickname + " 유저이름 조회 실패"));
                })
                .block();
    }

    private List<UserTradeListDto> getUserTradeList(String accessId, UserTradeRequestDto userTradeRequestDto) {
        List<UserTradeListDto> tradeList = webClient
                .mutate()
                .baseUrl(publicApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/openapi/fconline/v1.0/users/" + accessId + "/markets")
                        .queryParam("tradetype", userTradeRequestDto.getTradeType())
                        .queryParam("offset", userTradeRequestDto.getOffset())
                        .queryParam("limit", userTradeRequestDto.getLimit())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserTradeListDto>>() {
                })
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("거래 목록 조회 실패"));
                })
                .block();

        tradeList.forEach(v -> v.setTradeType(userTradeRequestDto.getTradeType()));

        return tradeList;
    }
}
