package com.simulation.fifa.api.batch.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.simulation.fifa.api.associations.entity.PlayerClubAssociation;
import com.simulation.fifa.api.associations.entity.PlayerPositionAssociation;
import com.simulation.fifa.api.associations.entity.PlayerSkillAssociation;
import com.simulation.fifa.api.batch.dto.CheckPlayerPriceDto;
import com.simulation.fifa.api.batch.dto.SeasonIdDto;
import com.simulation.fifa.api.batch.dto.SpIdDto;
import com.simulation.fifa.api.batch.dto.SpPositionDto;
import com.simulation.fifa.api.club.entity.Club;
import com.simulation.fifa.api.club.repository.ClubRepository;
import com.simulation.fifa.api.league.entity.League;
import com.simulation.fifa.api.league.repository.LeagueRepository;
import com.simulation.fifa.api.nation.entity.Nation;
import com.simulation.fifa.api.nation.repository.NationRepository;
import com.simulation.fifa.api.player.dto.PlayerBatchDto;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.entity.PreferredFootEnum;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import com.simulation.fifa.api.position.entity.Position;
import com.simulation.fifa.api.position.repository.PositionRepository;
import com.simulation.fifa.api.price.entity.PlayerPrice;
import com.simulation.fifa.api.price.repository.PlayerPriceRepository;
import com.simulation.fifa.api.season.entity.Season;
import com.simulation.fifa.api.season.repository.SeasonRepository;
import com.simulation.fifa.api.skill.entity.Skill;
import com.simulation.fifa.api.skill.repository.SkillRepository;
import com.simulation.fifa.util.RegexUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchService {
    public final int MAX_UPGRADE_VALUE = 10; // 선수 +10 단계 까지 저장
    public final int KEEP_DAYS = 30; //30일 동안의 가격 데이터 만 저장
    public final int ONCE_CREATE_PLAYER_COUNT = 5000; // 선수 배치 저장시 한번에 저장될 갯수
    public final int ONCE_CREATE_PLAYER_PRICE_COUNT = 6000; // 선수 데일리 시세 저장시 한번에 저장될 갯수
    private final List<Long> IGNORE_PLAYERS = List.of(
            //사이트 금액 데이터 오류
            300163155L,
            300167926L
    );

    @Value("${nexon.fifa-online.site-url}")
    private String siteUrl;
    @Value("${nexon.fifa-online.static-api-url}")
    private String staticApiUrl;

    private final WebClient webClient;
    private final SeasonRepository seasonRepository;
    private final ClubRepository clubRepository;
    private final LeagueRepository leagueRepository;
    private final PlayerRepository playerRepository;
    private final NationRepository nationRepository;
    private final PositionRepository positionRepository;
    private final SkillRepository skillRepository;
    private final PlayerPriceRepository playerPriceRepository;

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
        Map<Long, League> leagueMap = leagueRepository.findAll().stream().collect(Collectors.toMap(League::getId, league -> league));

        try {
            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            Elements elements = document.getElementsByClass("club_list").get(0).getElementsByTag("a");
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

    public void createNations() {
        List<Nation> nations = new ArrayList<>();
        try {
            Document document = Jsoup.connect(siteUrl + "/datacenter").get();
            nations = document.getElementsByClass("nationality_list").get(0).getElementsByTag("span").stream()
                    .map(Element::html)
                    .filter(v -> !v.equals("국적"))
                    .collect(Collectors.toSet())
                    .stream().map(nation -> Nation
                            .builder()
                            .nationName(nation)
                            .build()
                    )
                    .toList();

        } catch (IOException e) {
            log.error("선수 스킬 생성 오류 {0}", e);
        } finally {
            nationRepository.saveAll(nations);
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

    @Transactional
    public void createPrice(LocalDate date) {
        List<Long> notRenewalPlayers = playerPriceRepository.findByNotRenewalPrice(date);
        Set<PlayerPrice> playerPriceList = new HashSet<>();
        //int totalCount = Math.min(notRenewalPlayers.size(), ONCE_CREATE_PLAYER_PRICE_COUNT);
        int totalCount = notRenewalPlayers.size();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < notRenewalPlayers.size(); i++) {
                long playerId = notRenewalPlayers.get(i);
                //for (Long playerId : notRenewalPlayers.subList(0, totalCount)) {
                for (int j = 1; j <= MAX_UPGRADE_VALUE; j++) {
                    Document document = Jsoup.connect(siteUrl + "/datacenter/PlayerPriceGraph")
                            .data("spid", String.valueOf(playerId))
                            .data("n1Strong", String.valueOf(j))
                            .post();
                    long nowPrice = Long.parseLong(RegexUtil.extractNumbers(document.getElementsByClass("add_info").get(0).getElementsByTag("strong").get(0).html()));
                    // 현재 가격 설정
                    PlayerPrice nowPlayerPrice = PlayerPrice
                            .builder()
                            .player(Player.builder().id(playerId).build())
                            .price(nowPrice)
                            .grade(j)
                            .date(date)
                            .createAt(LocalDateTime.now())
                            .build();
                    playerPriceList.add(nowPlayerPrice);
                }
                log.info("시세 생성 진행률 {}", (float) i / totalCount * 100);
            }
        } catch (IOException e) {
            log.error("시세 생성 오류 {0}", e);
        } finally {
            long jsoupParseEndTime = System.currentTimeMillis();
            playerPriceRepository.saveAll(playerPriceList);

            log.info("JSOUP 파싱 시간: {}s", (jsoupParseEndTime - startTime) / 1000);
            log.info("{} 일자, {}건 시세 생성, 소요 시간 : {}s", date, totalCount, (System.currentTimeMillis() - startTime) / 1000);
            //log.info("남은 건수 {}건", Math.max(notRenewalPlayers.size() - ONCE_CREATE_PLAYER_PRICE_COUNT, 0));
        }
    }

    @Transactional
    public void deletePreviousPrice() {
        LocalDate previousDate = LocalDate.now().minusDays(KEEP_DAYS);
        long count = playerPriceRepository.deletePreviousPrice(previousDate);
        log.info("총 {} 건 시세 정보 삭제", count);
    }

    @Transactional
    public void createPlayers() {
        Set<SpIdDto> allSpIdList = getPlayerSpIdList();
        Set<Long> allPlayers = playerRepository.findAll().stream().map(Player::getId).collect(Collectors.toSet());
        Set<Long> remainPlayers = allSpIdList.stream().map(SpIdDto::getId).filter(v -> !allPlayers.contains(v)).collect(Collectors.toSet());
        createPlayers(remainPlayers);
    }

    @Transactional
    public void createPlayers(Set<Long> remainPlayers) {
        long startTime = System.currentTimeMillis();

        Set<Player> players = new LinkedHashSet<>();

        Map<Long, Season> seasonMap = seasonRepository.findAll().stream().collect(Collectors.toMap(Season::getId, season -> season));
        Map<String, Nation> nationMap = nationRepository.findAll().stream().collect(Collectors.toMap(Nation::getNationName, nation -> nation));
        Map<String, Position> positionMap = positionRepository.findAll().stream().collect(Collectors.toMap(Position::getPositionName, position -> position));
        Map<String, Skill> skillMap = skillRepository.findAll().stream().collect(Collectors.toMap(Skill::getSkillName, skill -> skill));
        Map<String, Club> clubMap = clubRepository.findAll().stream().collect(Collectors.toMap(Club::getClubName, club -> club));

        Set<SpIdDto> allSpIdList = getPlayerSpIdList();

        List<SpIdDto> spIdList = allSpIdList.stream()
                .filter(v -> remainPlayers.contains(v.getId()))
                .filter(v -> !IGNORE_PLAYERS.contains(v.getId()))
                .toList();

        int createSize = Math.min(ONCE_CREATE_PLAYER_COUNT, remainPlayers.size() - IGNORE_PLAYERS.size());

        for (SpIdDto spidDto : spIdList.subList(0, createSize)) {
            Long spId = spidDto.getId();
            Set<PlayerPositionAssociation> playerPositionAssociations;
            Set<PlayerClubAssociation> playerClubAssociations = new LinkedHashSet<>();
            Set<PlayerSkillAssociation> playerSkillAssociations = new LinkedHashSet<>();
            try {
                Document document = Jsoup.connect(siteUrl + "/DataCenter/PlayerInfo?spid=" + spId).get();

                PlayerBatchDto playerInfo = new PlayerBatchDto(spidDto.getId(), spidDto.getName());

                // 선수 주발, 약발 설정
                String[] foots = document.getElementsByClass("etc foot").get(0).html().split("–");
                for (String foot : foots) {
                    if (foot.contains("L")) {
                        playerInfo.setLeftFoot(Integer.parseInt(RegexUtil.extractNumbers(foot)));
                        if (foot.contains("strong")) {
                            playerInfo.setPreferredFoot(PreferredFootEnum.LEFT);
                        }
                        playerInfo.setPreferredFoot(foot.contains("strong") ? PreferredFootEnum.LEFT : PreferredFootEnum.RIGHT);
                    } else {
                        playerInfo.setRightFoot(Integer.parseInt(RegexUtil.extractNumbers(foot)));
                        if (foot.contains("strong")) {
                            playerInfo.setPreferredFoot(PreferredFootEnum.RIGHT);
                        }
                    }
                }

                // 급여
                playerInfo.setPay(Integer.parseInt(RegexUtil.extractNumbers(document.getElementsByClass("pay_side").get(0).html())));

                // 선수 능력치 설정
                Elements stats = document.getElementsByClass("content_bottom").get(0).getElementsByClass("ab");
                for (Element stat : stats) {
                    String name = stat.getElementsByClass("txt").get(0).html();
                    String value = RegexUtil.extractNumbers(stat.getElementsByClass("value").get(0).html());

                    playerInfo.setValueFromText(name, Integer.parseInt(value) + 3);
                }

                Player player = playerInfo.toEntity(playerInfo);

                // 선수 국가 설정
                Nation nation = nationMap.get(document.getElementsByClass("info_team").get(0).getElementsByClass("txt").get(0).html());
                player.updateNation(nation);

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
                                .overall(Integer.parseInt(el.getElementsByClass("value").get(0).html()) + 3)
                                .build()
                        )
                        .collect(Collectors.toSet());

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
                        .collect(Collectors.toSet());

                // 선수 스킬 설정
                playerSkillAssociations = document.getElementsByClass("skill_wrap").get(0).getElementsByTag("span")
                        .stream()
                        .filter(el -> !el.html().isEmpty())
                        .map(el -> PlayerSkillAssociation
                                .builder()
                                .player(player)
                                .skill(skillMap.get(el.html()))
                                .build()
                        )
                        .collect(Collectors.toSet());

                player.updatePlayerClubAssociations(playerClubAssociations);
                player.updatePlayerPositionAssociations(playerPositionAssociations);
                player.updatePlayerSkillAssociations(playerSkillAssociations);
                player.updatePriceList(makePriceHistories(player));

                players.add(player);

                log.info("선수 생성 진행률 {}%", (float) players.size() / createSize * 100);
            } catch (IOException e) {
                log.error("선수 생성 Jsoup 파싱 실패 {0}", e);
            } catch (Exception e) {
                log.error("선수 생성 오류 {0}", e);
            }
        }

        long domParsingEndTime = System.currentTimeMillis();
        playerRepository.saveAll(players);

        log.info("jsoup 파싱 총 {}s 소요", (domParsingEndTime - startTime) / 1000);
        log.info("총 {} 명 선수 생성 성공 {}s 소요", players.size(), (System.currentTimeMillis() - startTime) / 1000);
        log.info("남은 선수 : {} 명", spIdList.size() - players.size());
    }

    public List<CheckPlayerPriceDto> checkPrice() {
        return playerRepository.findCheckPrice();
    }

    private Set<PlayerPrice> makePriceHistories(Player player) {
        Set<PlayerPrice> playerPriceList = new HashSet<>();
        try {
            for (int i = 1; i <= MAX_UPGRADE_VALUE; i++) {
                Document document = Jsoup.connect(siteUrl + "/datacenter/PlayerPriceGraph")
                        .data("spid", String.valueOf(player.getId()))
                        .data("n1Strong", String.valueOf(i))
                        .post();
                long nowPrice = Long.parseLong(RegexUtil.extractNumbers(document.getElementsByClass("add_info").get(0).getElementsByTag("strong").get(0).html()));
                // 현재 가격 설정
                PlayerPrice nowPlayerPrice = PlayerPrice
                        .builder()
                        .player(player)
                        .price(nowPrice)
                        .grade(i)
                        .date(LocalDate.now())
                        .createAt(LocalDateTime.now())
                        .build();
                playerPriceList.add(nowPlayerPrice);

                String scriptText = document.select("script").get(1).html();
                int startIdx = scriptText.indexOf("var json1 = ");
                int endIdx = scriptText.indexOf("var option = {", startIdx);

                // json 문자열 데이터 파싱
                if (startIdx != -1 && endIdx != -1) {
                    String priceJsonStr = scriptText.substring(startIdx + 12, endIdx).trim();
                    JsonObject priceJson = JsonParser.parseString(priceJsonStr).getAsJsonObject();
                    JsonArray timeList = priceJson.getAsJsonArray("time");
                    JsonArray priceList = priceJson.getAsJsonArray("value");

                    LocalDateTime nowDateTime = LocalDateTime.now();

                    for (int y = timeList.size() - KEEP_DAYS; y < timeList.size(); y++) {
                        String timeStr = String.valueOf(timeList.get(y)).replace("\"", "");
                        String priceStr = String.valueOf(priceList.get(y)).replace("\"", "");

                        if (timeStr.isEmpty() || timeStr.equals("null") || priceStr.isEmpty() || priceStr.equals("null")) continue;
                        int month = Integer.parseInt(timeStr.split("\\.")[0]);
                        int day = Integer.parseInt(timeStr.split("\\.")[1]);

                        LocalDate date = LocalDate.of(LocalDate.now().getYear(), month, day);
                        long price = Long.parseLong(RegexUtil.extractNumbers(priceStr));

                        if (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) {
                            continue;
                        }
                        if (date.isBefore(LocalDate.now().minusDays(KEEP_DAYS - 1))) {
                            break;
                        }

                        PlayerPrice playerPrice = PlayerPrice
                                .builder()
                                .player(player)
                                .price(price)
                                .grade(i)
                                .date(date)
                                .createAt(nowDateTime)
                                .build();
                        playerPriceList.add(playerPrice);
                    }
                }
            }
        } catch (IOException e) {
            log.error("선수 시세 적용 Jsoup 파싱 실패 {0}", e);
        } catch (Exception e) {
            log.error("선수 시세 적용 실패 {1} : {}.", player.getId(), e);
        }

        return playerPriceList;
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

    private Set<SpIdDto> getPlayerSpIdList() {
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
                .bodyToMono(new ParameterizedTypeReference<LinkedHashSet<SpIdDto>>() {
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
        Set<SpIdDto> spIdList = getPlayerSpIdList();

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
