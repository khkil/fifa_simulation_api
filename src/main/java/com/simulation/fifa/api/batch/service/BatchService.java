package com.simulation.fifa.api.batch.service;

import com.simulation.fifa.api.batch.dto.SeasonIdDto;
import com.simulation.fifa.api.batch.dto.SpPositionDto;
import com.simulation.fifa.api.club.entity.Club;
import com.simulation.fifa.api.club.repository.ClubRepository;
import com.simulation.fifa.api.league.entity.League;
import com.simulation.fifa.api.league.repository.LeagueRepository;
import com.simulation.fifa.api.player.dto.PlayerDto;
import com.simulation.fifa.api.batch.dto.SpIdDto;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import com.simulation.fifa.api.position.domain.Position;
import com.simulation.fifa.api.position.repository.PositionRepository;
import com.simulation.fifa.api.season.entity.Season;
import com.simulation.fifa.api.season.repository.SeasonRepository;
import com.simulation.fifa.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BatchService {
    @Value("${nexon.fifa-online.site-url}")
    private String siteUrl;
    @Value("${nexon.fifa-online.static-api-url}")
    private String staticApiUrl;

    @Autowired
    WebClient webClient;
    @Autowired
    SeasonRepository seasonRepository;
    @Autowired
    ClubRepository clubRepository;
    @Autowired
    LeagueRepository leagueRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    PositionRepository positionRepository;

    public void createLeagues() {
        List<League> leagues = new ArrayList<>();
        try {
            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            Elements elements = document.getElementsByClass("wrap_league").get(0).getElementsByTag("a");
            for (Element el : elements) {
                long leagueId = parseLeagueId(el);
                if (leagueId > 0) continue;

                String leagueName = el.getElementsByTag("span").html();
                League league = League
                        .builder()
                        .id(leagueId)
                        .leagueName(leagueName)
                        .build();

                leagues.add(league);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        leagueRepository.saveAll(leagues);
    }

    public void createClubs() {

        List<Club> clubs = new ArrayList<>();
        Map<Long, League> leagueMap = leagueRepository.findAll().stream().collect(Collectors.toMap(League::getId, League -> League));

        try {
            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            Elements elements = document.getElementsByClass("club_list").get(0).getElementsByTag("a");
            for (Element el : elements) {
                long leagueId = parseLeagueId(el);

                if (leagueId <= 0) continue;

                League league = leagueMap.get(leagueId);
                if (league == null) continue;

                long clubId = Long.parseLong(RegexUtil.extractNumbers(el.attr("onclick")));
                String clubName = el.getElementsByTag("span").html();

                Club club = Club
                        .builder()
                        .id(clubId)
                        .clubName(clubName)
                        .league(league)
                        .build();

                clubs.add(club);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        clubRepository.saveAll(clubs);
    }

    public void createPositions() {
        List<Position> positions = getPositions().stream().map(p -> p.toEntity(p)).toList();
        positionRepository.saveAll(positions);
    }

    public void createSeasons() {
        List<Season> seasons = getSeasonIdList().stream().map(v -> v.toEntity(v)).toList();
        seasonRepository.saveAll(seasons);
    }

    public void createPlayers() {
        List<Player> players = new ArrayList<>();

        Map<Long, Season> seasonMap = seasonRepository.findAll().stream().collect(Collectors.toMap(Season::getId, season -> season));
        Map<Long, Position> positionMap = positionRepository.findAll().stream().collect(Collectors.toMap(Position::getId, position -> position));

        List<SpIdDto> spIdList = getPlayerSpidList().subList(0, 50);

        for (SpIdDto spidDto : spIdList) {
            Long spId = spidDto.getId();
            try {
                Document document = Jsoup.connect(siteUrl + "/DataCenter/PlayerInfo?spid=" + spId).get();
                //Element positionEl = document.getElementsByClass("position_tabs").get(0);
                Elements stats = document.getElementsByClass("content_bottom").get(0).getElementsByClass("ab");
                PlayerDto.Detail playerInfo = new PlayerDto.Detail(spidDto.getId(), spidDto.getName());

                // 선수 능력치 설정
                for (Element stat : stats) {
                    String name = stat.getElementsByClass("txt").get(0).html();
                    String value = RegexUtil.extractNumbers(stat.getElementsByClass("value").get(0).html());

                    playerInfo.setValueFromText(name, Integer.parseInt(value));
                }

                // 선수 포지션 설정

                // 선수 스킬 설정
                List<String> skills = document.getElementsByClass("skill_wrap").get(0).getElementsByTag("span").stream().map(Element::html).toList();
                playerInfo.setSkills(String.join(", ", skills));

                Player player = playerInfo.toEntity(playerInfo);

                // 선수 시즌 설정
                Long seasonId = Long.parseLong(spId.toString().substring(0, 3));
                Season season = seasonMap.get(seasonId);
                player.updateSeason(season);

                players.add(player);

            } catch (IOException e) {
                System.out.println(e);
            }

            playerRepository.saveAll(players);
        }
    }

    private List<SeasonIdDto> getSeasonIdList() {
        return webClient
                .mutate()
                .baseUrl(staticApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/latest/seasonid.json")
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SeasonIdDto>>() {
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }

    private List<SpPositionDto> getPositions() {
        return webClient
                .mutate()
                .baseUrl(staticApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/latest/spposition.json")
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SpPositionDto>>() {
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }

    private List<SpIdDto> getPlayerSpidList() {
        return webClient
                .mutate()
                .baseUrl(staticApiUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fconline/latest/spid.json")
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SpIdDto>>() {
                })
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }

    private long parseLeagueId(Element element) {
        try {
            return Long.parseLong(element.attr("data-no"));
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            return -1;
        }
    }
}
