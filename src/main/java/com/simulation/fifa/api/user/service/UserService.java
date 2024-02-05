package com.simulation.fifa.api.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simulation.fifa.api.batch.service.BatchService;
import com.simulation.fifa.api.user.dto.squad.SquadDto;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import com.simulation.fifa.api.position.entity.Position;
import com.simulation.fifa.api.position.repository.PositionRepository;
import com.simulation.fifa.api.price.dto.PlayerRecentPriceDto;
import com.simulation.fifa.api.price.repository.PlayerPriceRepository;
import com.simulation.fifa.api.season.dto.SeasonListDto;
import com.simulation.fifa.api.season.entity.Season;
import com.simulation.fifa.api.user.dto.UserDto;
import com.simulation.fifa.api.user.dto.UserMatchTopRankDto;
import com.simulation.fifa.api.user.dto.match.*;
import com.simulation.fifa.api.user.dto.trade.UserTradeListDto;
import com.simulation.fifa.api.user.dto.trade.UserTradeRequestDto;
import com.simulation.fifa.util.SeleniumUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${nexon.fc-online.open-api-url}")
    private String openApiUrl;
    @Value("${nexon.fc-online.static-api-url}")
    private String staticApiUrl;
    @Value("${nexon.fc-online.site-url}")
    private String siteUrl;
    @Value("${nexon.fc-online.api-key.name}")
    private String apiKeyName;
    @Value("${nexon.fc-online.api-key}")
    private String apiKey;

    private final WebClient webClient;
    private final PlayerRepository playerRepository;
    private final PlayerPriceRepository playerPriceRepository;
    private final PositionRepository positionRepository;
    private final BatchService batchService;

    public UserDto findUserInfo(String nickname) {
        UserDto user = getUserInfo(nickname);
        List<DivisionDto> divisions = getDivisions();

        Map<Integer, String> matchTypeMap = getMatchTypes().stream().collect(Collectors.toMap(MatchTypeDto::getMatchtype, MatchTypeDto::getDesc));
        Map<Integer, String> divisionMap = divisions.stream().collect(Collectors.toMap(DivisionDto::getDivisionId, DivisionDto::getDivisionName));

        List<UserMatchTopRankDto.origin> topRanksOrigin = webClient
                .mutate()
                .baseUrl(openApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/v1/user/maxdivision")
                        .queryParam("ouid", user.getOuid())
                        .build()
                )
                .header(apiKeyName, apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserMatchTopRankDto.origin>>() {
                })
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("매치 목록 조회 실패"));
                })
                .block();

        List<UserMatchTopRankDto.desc> topRanks = topRanksOrigin.stream().map(v -> UserMatchTopRankDto.desc
                .builder()
                .matchTypeDesc(matchTypeMap.get(v.getMatchType()))
                .divisionName(divisionMap.get(v.getDivision()))
                .divisionImageUrl("https://ssl.nexon.com/s2/game/fo4/obt/rank/large/update_2009/ico_rank" + divisions.stream().map(DivisionDto::getDivisionId).toList().indexOf(v.getDivision()) + ".png")
                .achievementDate(v.getAchievementDate())
                .build()
        ).toList();

        user.setTopRanks(topRanks);

        return user;
    }

    public List<UserTradeListDto> findUserTrades(String nickname, UserTradeRequestDto userTradeRequestDto) {
        UserDto user = getUserInfo(nickname);

        List<UserTradeListDto> buyList = getUserTradeList(user.getOuid(), UserTradeRequestDto
                .builder()
                .tradeType("buy")
                .offset(userTradeRequestDto.getOffset())
                .limit(userTradeRequestDto.getLimit())
                .build()
        );

        List<UserTradeListDto> sellList = getUserTradeList(user.getOuid(), UserTradeRequestDto
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

            // 데이터 생성 했다면 영속성 캐시에서 조회
            batchService.createPlayers(missedPlayers);
            players = playerRepository.findAllByIdIn(spIdList);
        }

        Map<Long, Player> playerMap = players.stream().collect(Collectors.toMap(Player::getId, player -> player));
        for (UserTradeListDto trade : joinedList) {
            Player player = playerMap.get(trade.getSpid());
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

        return joinedList;
    }

    public SquadDto findUserSquad(String nickname) {
        ObjectMapper objectMapper = new ObjectMapper();
        WebDriver webDriver = SeleniumUtil.init();

        String userHiddenNo = getHiddenGuestNo(nickname);

        try {
            webDriver.get("https://fconline.nexon.com/profile/squad/popup/" + userHiddenNo);
            JavascriptExecutor js = (JavascriptExecutor) webDriver;

            double time = 0;
            while (true) {
                Map<String, Object> squadState = objectMapper.convertValue(js.executeScript("return squadState;"), Map.class);
                List<?> players = objectMapper.convertValue(squadState.get("players"), List.class);

                time += 100;

                if (time == 5000) { // 5초간 쓰레드 대기시 에러 처리
                    throw new RuntimeException("스쿼드 조회 소요시간 초과");
                } else if (players.isEmpty()) {
                    Thread.sleep(100);
                } else {
                    break;
                }
            }

            log.info("스쿼드 조회 소요시간 {}s", time / 1000);

            Map<String, Object> squadState = objectMapper.convertValue(js.executeScript("return squadState;"), Map.class); // 전체값 디버깅용
            SquadDto squad = objectMapper.convertValue(js.executeScript("return squadState;"), SquadDto.class);

            LocalDate start = LocalDate.now().minusDays(BatchService.KEEP_DAYS);
            LocalDate end = LocalDate.now().minusDays(1);
            List<SquadDto.TotalPrice> priceList = playerPriceRepository.findPlayerPriceByIdsAndDateBetween(squad.getPlayers(), start, end);

            squad.updateTotalPrice(priceList);

            return squad;

        } catch (Exception e) {
            throw new RuntimeException("공홈 유저 스쿼드 조회 실패 \n", e);
        } finally {
            webDriver.quit();
        }
    }

    public List<UserMatchDto> findUserMatchList(String nickname, UserMatchRequestDto userMatchRequestDto) {
        UserDto user = getUserInfo(nickname);

        int page = userMatchRequestDto.getPage();
        int limit = 15;
        int offset = (page - 1) * limit;

        List<String> matchIds = getUserMatchList(user.getOuid(), UserMatchRequestDto
                .builder()
                .matchType(userMatchRequestDto.getMatchType())
                .offset(offset)
                .limit(limit)
                .build()
        );
        return matchIds.stream()
                .map(matchId -> {
                    UserMatchDetailDto matchDetail = getUserMatchDetail(matchId);
                    return UserMatchDto
                            .builder()
                            .matchId(matchDetail.getMatchId())
                            .matchDate(matchDetail.getMatchDate())
                            .matchType(matchDetail.getMatchType())
                            .users(matchDetail.getMatchInfo().stream()
                                    .map(matchInfo -> UserMatchDto.User
                                            .builder()
                                            .accessId(matchInfo.getAccessId())
                                            .nickname(matchInfo.getNickname())
                                            .goal(matchInfo.getShoot().getGoalTotal())
                                            .matchResult(matchInfo.getMatchDetail().getMatchResult())
                                            .controller(matchInfo.getMatchDetail().getController())
                                            .build()
                                    ).toList()
                            )
                            .build();
                })
                .toList();
    }

    public UserMatchDetailDto findUserMatchByMatchId(String matchId) {
        UserMatchDetailDto matchDetail = getUserMatchDetail(matchId);
        Map<Long, String> positionMap = positionRepository.findAll().stream().collect(Collectors.toMap(Position::getId, Position::getPositionName));

        for (UserMatchDetailDto.MatchInfo matchInfo : matchDetail.getMatchInfo()) {
            List<UserMatchDetailDto.MatchInfo.Player> players = matchInfo.getPlayer()
                    .stream().filter(v -> v.getSpPosition() != 28).toList() // SUB 포지션 제외
                    .stream().sorted((a, b) -> b.getSpPosition() > a.getSpPosition() ? 1 : -1).toList();
            List<Long> spIdList = players.stream().map(UserMatchDetailDto.MatchInfo.Player::getSpId).toList();
            List<Integer> gradeList = players.stream().map(UserMatchDetailDto.MatchInfo.Player::getSpGrade).toList();


            if (!players.isEmpty()) {
                List<PlayerRecentPriceDto> priceList = playerPriceRepository.findRecentPriceList(spIdList, gradeList);

                players.forEach(p -> {
                    p.setPositionName(positionMap.get(p.getSpPosition()));

                    priceList.stream()
                            .filter(price ->
                                    price.getPlayerId().equals(p.getSpId()) && price.getGrade().equals(p.getSpGrade())
                            ).findAny().ifPresentOrElse(v -> {
                                        p.setPrice(v.getPrice());
                                        p.setName(v.getPlayerName());
                                        p.setSeasonId(v.getSeasonId());
                                        p.setSeasonImageUrl(v.getSeasonImageUrl());
                                    }, () -> log.error("선수 가격이 존재하지 않습니다 {}", p.getSpId())
                            );
                });
            }

            matchInfo.setPlayer(players);
        }
        return matchDetail;
    }

    private String getHiddenGuestNo(String nickname) {
        try {
            Document document = Jsoup.connect(siteUrl + "/profile/common/PopProfile")
                    .data("strCharacterName", nickname.trim())
                    .get();

            String ogUrl = document.select("meta[property=og:url]").attr("content");
            String[] arr = ogUrl.split("/");
            return arr[arr.length - 1];

        } catch (IOException e) {
            throw new RuntimeException("유저 히든 아이디 조회 실패", e);
        }
    }

    private List<String> getUserMatchList(String accessId, UserMatchRequestDto userMatchRequestDto) {
        return webClient
                .mutate()
                .baseUrl(openApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/v1/user/match")
                        .queryParam("ouid", accessId)
                        .queryParam("matchtype", userMatchRequestDto.getMatchType())
                        .queryParam("offset", userMatchRequestDto.getOffset())
                        .queryParam("limit", userMatchRequestDto.getLimit())
                        .build()
                )
                .header(apiKeyName, apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("매치 목록 조회 실패"));
                })
                .block();
    }


    private UserDto getUserInfo(String nickname) {
        return webClient
                .mutate()
                .baseUrl(openApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/v1/id")
                        .queryParam("nickname", nickname)
                        .build()
                )
                .header(apiKeyName, apiKey)
                .retrieve()
                .bodyToMono(UserDto.class)
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("닉네임 : " + nickname + " 유저이름 조회 실패"));
                })
                .block();
    }

    private UserMatchDetailDto getUserMatchDetail(String matchId) {
        return webClient
                .mutate()
                .baseUrl(openApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/v1/match-detail")
                        .queryParam("matchid", matchId)
                        .build()
                )
                .header(apiKeyName, apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<UserMatchDetailDto>() {
                })
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("매치 상세정보 조회 실패"));
                })
                .block();
    }

    private List<UserTradeListDto> getUserTradeList(String ouid, UserTradeRequestDto userTradeRequestDto) {
        List<UserTradeListDto> tradeList = webClient
                .mutate()
                .baseUrl(openApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/v1/user/trade")
                        .queryParam("tradetype", userTradeRequestDto.getTradeType())
                        .queryParam("offset", userTradeRequestDto.getOffset())
                        .queryParam("limit", userTradeRequestDto.getLimit())
                        .build()
                )
                .header("x-nxopen-api-key", apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserTradeListDto>>() {
                })
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("거래 목록 조회 실패"));
                })
                .block();

        assert tradeList != null;
        tradeList.forEach(v -> v.setTradeType(userTradeRequestDto.getTradeType()));

        return tradeList;
    }

    private List<MatchTypeDto> getMatchTypes() {
        return webClient
                .mutate()
                .baseUrl(staticApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/latest/matchtype.json")
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MatchTypeDto>>() {
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }

    private List<DivisionDto> getDivisions() {
        return webClient
                .mutate()
                .baseUrl(staticApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/latest/division.json")
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DivisionDto>>() {
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }
}
