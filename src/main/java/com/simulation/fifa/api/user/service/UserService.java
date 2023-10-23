package com.simulation.fifa.api.user.service;

import com.simulation.fifa.api.batch.dto.SeasonIdDto;
import com.simulation.fifa.api.batch.service.BatchService;
import com.simulation.fifa.api.nation.entity.Nation;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @Value("${nexon.fifa-online.static-api-url}")
    private String staticApiUrl;
    @Value("${nexon.fifa-online.api-key}")
    private String apiKey;

    @Autowired
    WebClient webClient;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    PlayerPriceRepository playerPriceRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    BatchService batchService;

    public UserDto findUserInfo(String nickname) {
        UserDto user = getUserInfo(nickname);
        List<DivisionDto> divisions = getDivisions();

        Map<Integer, String> matchTypeMap = getMatchTypes().stream().collect(Collectors.toMap(MatchTypeDto::getMatchtype, MatchTypeDto::getDesc));
        Map<Integer, String> divisionMap = divisions.stream().collect(Collectors.toMap(DivisionDto::getDivisionId, DivisionDto::getDivisionName));

        List<UserMatchTopRankDto.origin> topRanksOrigin = webClient
                .mutate()
                .baseUrl(publicApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/openapi/fconline/v1.0/users/" + user.getAccessId() + "/maxdivision")
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, apiKey)
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

    public List<UserSquadDto> searchUserSquad(String nickname) {
        UserDto searchedUser = getUserInfo(nickname);
        List<String> matchIds = getUserMatchList(searchedUser.getAccessId(), UserMatchRequestDto
                .builder()
                .matchType(50) // 공식경기
                .offset(0)
                .limit(100)
                .build()
        );

        List<UserMatchDetailDto.MatchInfo.Player> matchPlayers = new ArrayList<>();
        for (String matchId : matchIds) {
            if (!matchPlayers.isEmpty()) {
                break;
            }
            log.info("유저 매치 상세 조회 시작");
            UserMatchDetailDto matchDetail = getUserMatchDetail(matchId);
            UserMatchDetailDto.MatchInfo searchedUserMatchInfo = matchDetail.getMatchInfo()
                    .stream().filter(v -> v.getAccessId().equals(searchedUser.getAccessId())).toList()
                    .stream().findAny().orElseThrow(() -> new UsernameNotFoundException("검색한 유저의 경기가 아닙니다."));

            matchPlayers.addAll(searchedUserMatchInfo.getPlayer());
        }

        if (matchPlayers.isEmpty()) {
            throw new RuntimeException("검색한 유저의 선수 정보가 존재하지 않습니다.");
        }

        List<Long> spIds = matchPlayers.stream().map(UserMatchDetailDto.MatchInfo.Player::getSpId).toList();
        List<Integer> grades = matchPlayers.stream().map(UserMatchDetailDto.MatchInfo.Player::getSpGrade).toList();

        List<Player> players = playerRepository.findAllByIdIn(spIds);

        if (players.size() < matchPlayers.size()) {
            List<Player> finalPlayers = players;
            Set<Long> missedPlayers = matchPlayers.stream()
                    .map(UserMatchDetailDto.MatchInfo.Player::getSpId)
                    .filter(v -> !finalPlayers.stream().map(Player::getId).toList().contains(v))
                    .collect(Collectors.toSet());

            batchService.createPlayers(missedPlayers);

            players = playerRepository.findAllByIdIn(spIds);
        }

        Map<Long, Player> playerMap = players.stream().collect(Collectors.toMap(Player::getId, p -> p));
        Map<Long, Long> priceMap = playerPriceRepository.findRecentPriceList(spIds, grades).stream().collect(Collectors.toMap(PlayerRecentPriceDto::getPlayerId, PlayerRecentPriceDto::getPrice));
        Map<Long, String> positionMap = positionRepository.findAll().stream().collect(Collectors.toMap(Position::getId, Position::getPositionName));
        Map<Long, Integer> gradeMap = matchPlayers.stream().collect(Collectors.toMap(UserMatchDetailDto.MatchInfo.Player::getSpId, UserMatchDetailDto.MatchInfo.Player::getSpGrade));

        return matchPlayers.stream().sorted((a, b) -> Math.toIntExact(a.getSpPosition() - b.getSpPosition())).map(v -> UserSquadDto
                .builder()
                .playerId(playerMap.get(v.getSpId()).getId())
                .playerName(playerMap.get(v.getSpId()).getName())
                .positionName(positionMap.get(v.getSpPosition()))
                .pay(playerMap.get(v.getSpId()).getPay())
                .seasonId(playerMap.get(v.getSpId()).getSeason().getId())
                .seasonName(playerMap.get(v.getSpId()).getSeason().getName())
                .seasonImgUrl(playerMap.get(v.getSpId()).getSeason().getImageUrl())
                .grade(gradeMap.get(v.getSpId()))
                .recentPrice(priceMap.get(v.getSpId()))
                .build()
        ).toList();
    }

    public List<String> getUserMatchList(String accessId, UserMatchRequestDto userMatchRequestDto) {

        return webClient
                .mutate()
                .baseUrl(publicApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/openapi/fconline/v1.0/users/" + accessId + "/matches")
                        .queryParam("matchtype", userMatchRequestDto.getMatchType())
                        .queryParam("offset", userMatchRequestDto.getOffset())
                        .queryParam("limit", userMatchRequestDto.getLimit())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, apiKey)
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

    private UserMatchDetailDto getUserMatchDetail(String matchId) {
        return webClient
                .mutate()
                .baseUrl(publicApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/openapi/fconline/v1.0/matches/" + matchId.trim())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<UserMatchDetailDto>() {
                })
                .onErrorResume(error -> {
                    log.error("{0}", error);
                    return Mono.error(new RuntimeException("매치 상세정보 조회 실패"));
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
