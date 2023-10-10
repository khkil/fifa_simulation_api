package com.simulation.fifa.api.batch.service;

import com.simulation.fifa.api.association.entity.PlayerClubAssociation;
import com.simulation.fifa.api.association.entity.PlayerPositionAssociation;
import com.simulation.fifa.api.association.entity.PlayerSkillAssociation;
import com.simulation.fifa.api.association.repository.PlayerClubAssociationRepository;
import com.simulation.fifa.api.association.repository.PlayerPositionAssociationRepository;
import com.simulation.fifa.api.association.repository.PlayerSkillAssociationRepository;
import com.simulation.fifa.api.batch.dto.SeasonIdDto;
import com.simulation.fifa.api.batch.dto.SpIdDto;
import com.simulation.fifa.api.batch.dto.SpPositionDto;
import com.simulation.fifa.api.club.entity.Club;
import com.simulation.fifa.api.club.repository.ClubRepository;
import com.simulation.fifa.api.league.entity.League;
import com.simulation.fifa.api.league.repository.LeagueRepository;
import com.simulation.fifa.api.player.dto.PlayerBatchDto;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import com.simulation.fifa.api.position.domain.Position;
import com.simulation.fifa.api.position.repository.PositionRepository;
import com.simulation.fifa.api.season.entity.Season;
import com.simulation.fifa.api.season.repository.SeasonRepository;
import com.simulation.fifa.api.skill.entity.Skill;
import com.simulation.fifa.api.skill.repository.SkillRepository;
import com.simulation.fifa.util.RegexUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.jdbc.core.JdbcTemplate;
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
@RequiredArgsConstructor
public class BatchService {
    private final JdbcTemplate jdbcTemplate;

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
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    PlayerPositionAssociationRepository playerPositionAssociationRepository;
    @Autowired
    PlayerClubAssociationRepository playerClubAssociationRepository;
    @Autowired
    PlayerSkillAssociationRepository playerSkillAssociationRepository;

    public void createLeagues() {
        List<League> leagues = new ArrayList<>();
        try {
            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            Elements elements = document.getElementsByClass("wrap_league").get(0).getElementsByTag("a");
            for (Element el : elements) {
                long leagueId = parseLeagueId(el);
                // 리그 0(해당없음) or -1(리그가 없거나 파싱오류 발생) 일 경우 skip
                if (leagueId <= 0) continue;

                String leagueName = el.getElementsByTag("span").html();
                League league = League
                        .builder()
                        .id(leagueId)
                        .leagueName(leagueName)
                        .build();

                leagues.add(league);
            }
        } catch (IOException e) {
            log.error("리그 생성 오류 {0}", e);
        } finally {
            leagueRepository.saveAll(leagues);
        }
    }

    public void createClubs() {

        List<Club> clubs = new ArrayList<>();
        Map<Long, League> leagueMap = leagueRepository.findAll().stream().collect(Collectors.toMap(League::getId, League -> League));

        try {
            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            Elements elements = document.getElementsByClass("club_list").get(0).getElementsByTag("a");
            int size = elements.size();
            for (Element el : elements) {
                long leagueId = parseLeagueId(el);

                if (leagueId == 0) continue;

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
        } finally {
            clubRepository.saveAll(clubs);
        }
    }

    public void createPositions() {
        List<Position> positions = getPositions().stream().map(p -> p.toEntity(p)).toList();
        positionRepository.saveAll(positions);
    }

    public void createSkills() {
        List<Skill> skills = new ArrayList<>();
        try {
            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            skills = document.getElementsByClass("search_po_ab").get(0).getElementsByTag("span").stream()
                    .map(Element::html)
                    .filter(v -> !v.equals("보유 특성1"))
                    .collect(Collectors.toSet())
                    .stream().map(skill -> Skill
                            .builder()
                            .skillName(skill)
                            .build()
                    )
                    .toList();

        } catch (IOException e) {
            log.error("선수 스킬 생성 오류 {0}", e);
        } finally {
            skillRepository.saveAll(skills);
        }
    }

    public void createSeasons() {
        List<Season> seasons = getSeasonIdList().stream().map(v -> v.toEntity(v)).toList();
        seasonRepository.saveAll(seasons);
    }

    public void createPlayers() {
        List<Player> players = new ArrayList<>();
        List<PlayerPositionAssociation> playerPositionAssociations = new ArrayList<>();
        List<PlayerClubAssociation> playerClubAssociations = new ArrayList<>();
        List<PlayerSkillAssociation> playerSkillAssociations = new ArrayList<>();

        Map<Long, Season> seasonMap = seasonRepository.findAll().stream().collect(Collectors.toMap(Season::getId, season -> season));
        Map<String, Position> positionMap = positionRepository.findAll().stream().collect(Collectors.toMap(Position::getPositionName, position -> position));
        Map<String, Skill> skillMap = skillRepository.findAll().stream().collect(Collectors.toMap(Skill::getSkillName, skill -> skill));
        Map<String, Club> clubMap = clubRepository.findAll().stream().collect(Collectors.toMap(Club::getClubName, club -> club));

        List<SpIdDto> spIdList = getPlayerSpidList().subList(0, 30);

        for (SpIdDto spidDto : spIdList) {
            Long spId = spidDto.getId();
            try {
                Document document = Jsoup.connect(siteUrl + "/DataCenter/PlayerInfo?spid=" + spId).get();

                PlayerBatchDto playerInfo = new PlayerBatchDto(spidDto.getId(), spidDto.getName());

                // 선수 능력치 설정
                Elements stats = document.getElementsByClass("content_bottom").get(0).getElementsByClass("ab");
                for (Element stat : stats) {
                    String name = stat.getElementsByClass("txt").get(0).html();
                    String value = RegexUtil.extractNumbers(stat.getElementsByClass("value").get(0).html());

                    playerInfo.setValueFromText(name, Integer.parseInt(value));
                }

                Player player = playerInfo.toEntity(playerInfo);

                // 선수 시즌 설정
                Long seasonId = Long.parseLong(spId.toString().substring(0, 3));
                Season season = seasonMap.get(seasonId);
                player.updateSeason(season);


                // 선수 포지션 설정
                playerPositionAssociations = document.getElementsByClass("info_ab").get(0).getElementsByClass("position")
                        .stream()
                        .map(el -> PlayerPositionAssociation
                                .builder()
                                .player(player)
                                .position(positionMap.get(el.getElementsByClass("txt").get(0).html()))
                                .build()
                        )
                        .toList();


                // 선수 클럽 설정
                playerClubAssociations = document.getElementsByClass("data_detail_club").get(0).getElementsByTag("li")
                        .stream()
                        .map(el -> {
                                    String clubName = el.getElementsByClass("club").html();
                                    String[] years = el.getElementsByClass("year").html().split("~");

                                    return PlayerClubAssociation
                                            .builder()
                                            .player(player)
                                            .startYear(years.length < 1 ? null : Integer.parseInt(years[0].trim()))
                                            .endYear(years.length < 2 ? null : Integer.parseInt(years[1].trim()))
                                            .club(clubMap.get(clubName))
                                            .build();
                                }
                        )
                        .toList();


                // 선수 스킬 설정
                playerSkillAssociations = document.getElementsByClass("skill_wrap").get(0).getElementsByTag("span")
                        .stream()
                        //.map(Element::html)
                        .map(el -> PlayerSkillAssociation
                                .builder()
                                .player(player)
                                .skill(skillMap.get(el.html()))
                                .build()
                        )
                        .toList();

                players.add(player);
            } catch (IOException e) {
                log.error("선수 생성 오류 {0}", e);
            } finally {
                playerRepository.saveAll(players);
                playerPositionAssociationRepository.saveAll(playerPositionAssociations);
                playerClubAssociationRepository.saveAll(playerClubAssociations);
                playerSkillAssociationRepository.saveAll(playerSkillAssociations);
            }
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
            log.error("리그 아이디 파싱 오류 {0}", e);
            return -1;
        }
    }

    public void bulkTest() {
        long beforeTime = System.currentTimeMillis();

        List<Player> players = new ArrayList<>();
        List<SpIdDto> spIdList = getPlayerSpidList().subList(0, 500);

        try {
            //To-do 데이터 셋팅 후 신규 시즌 선수 추가시 skip 하는 로직 구현
            for (SpIdDto spidDto : spIdList) {
                Long spId = spidDto.getId();
                Document document = Jsoup.connect(siteUrl + "/DataCenter/PlayerInfo?spid=" + spId).get();
                log.info("document {}", (System.currentTimeMillis() - beforeTime) / 1000);

                PlayerBatchDto playerInfo = new PlayerBatchDto(spidDto.getId(), spidDto.getName());

                // 선수 능력치 설정
                Elements stats = document.getElementsByClass("content_bottom").get(0).getElementsByClass("ab");
                for (Element stat : stats) {
                    String name = stat.getElementsByClass("txt").get(0).html();
                    String value = RegexUtil.extractNumbers(stat.getElementsByClass("value").get(0).html());

                    playerInfo.setValueFromText(name, Integer.parseInt(value));
                }

                Player player = playerInfo.toEntity(playerInfo);

                players.add(player);
            }
        } catch (IOException e) {
            log.error("선수 생성 오류 {0}", e);
        } finally {
            log.info("document all {}", (System.currentTimeMillis() - beforeTime) / 1000);
            /*
            String sql = "INSERT INTO player (id, name) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Long id = players.get(i).getId();
                    String name = players.get(i).getName();
                    ps.setLong(1, id);
                    ps.setString(2, name);
                }

                @Override
                public int getBatchSize() {
                    return players.size();
                }
            });
            beforeTime = System.currentTimeMillis();
            log.info("JDBC {}", (System.currentTimeMillis() - beforeTime) / 1000);*/


            playerRepository.saveAll(players);
            log.info("JPA {}", (System.currentTimeMillis() - beforeTime) / 1000);
        }
    }
}
