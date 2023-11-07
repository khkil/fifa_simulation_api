package com.simulation.fifa.api.player.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SquadDto {
    private String formation;
    private String maintotalPrice;
    private Map<String, Integer> ovr;
    private List<Player> players;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Player {
        private String spid;
        private String name;
        private String position;
        private String buildUp;
        private String price;
        private double x;
        private double y;
    }
}
