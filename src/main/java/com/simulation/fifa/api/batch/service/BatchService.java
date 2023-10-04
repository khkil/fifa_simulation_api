package com.simulation.fifa.api.batch.service;

import com.simulation.fifa.api.batch.dto.SeasonIdDto;
import com.simulation.fifa.api.club.entity.Club;
import com.simulation.fifa.api.club.repository.ClubRepository;
import com.simulation.fifa.api.league.entity.League;
import com.simulation.fifa.api.league.repository.LeagueRepository;
import com.simulation.fifa.api.player.dto.AbilityDto;
import com.simulation.fifa.api.batch.dto.SpIdDto;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.repository.PlayerRepository;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public void createLeagues() {
        List<League> leagues = new ArrayList<>();
        try {

            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            Elements elements = document.getElementsByClass("wrap_league").get(0).getElementsByTag("a");
            for (Element el : elements) {
                long leagueId = parseLeagueId(el);
                if (leagueId > 0) {
                    String leagueName = el.getElementsByTag("span").html();
                    League league = League
                            .builder()
                            .id(leagueId)
                            .leagueName(leagueName)
                            .build();

                    leagues.add(league);
                }
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

    public void createSeasons() {
        List<Season> seasons = getSeasonIdList().stream().map(v -> Season
                .builder()
                .id(v.getSeasonId())
                .name(v.getClassName())
                .imageUrl(v.getSeasonImg())
                .build()
        ).toList();

        seasonRepository.saveAll(seasons);
    }

    public void createPlayers() {
        List<Player> players = new ArrayList<>();

        Map<Long, Season> seasonMap = seasonRepository.findAll().stream().collect(Collectors.toMap(Season::getId, season -> season));
        List<SpIdDto> spIdList = getPlayerSpidList().subList(0, 10);


        for (SpIdDto spidDto : spIdList) {
            Long spId = spidDto.getId();
            try {
                Document document = Jsoup.connect(siteUrl + "/DataCenter/PlayerInfo?spid=" + spId).get();
                //Element positionEl = document.getElementsByClass("position_tabs").get(0);
                Elements abilities = document.getElementsByClass("content_bottom").get(0).getElementsByClass("ab");
                AbilityDto.Detail abilityInfo = new AbilityDto.Detail(spidDto.getId(), spidDto.getName());

                for (Element ability : abilities) {
                    String name = ability.getElementsByClass("txt").get(0).html();
                    String value = RegexUtil.extractNumbers(ability.getElementsByClass("value").get(0).html());

                    abilityInfo.setValueFromText(name, Integer.parseInt(value));
                }
                Player player = abilityInfo.toEntity();

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
