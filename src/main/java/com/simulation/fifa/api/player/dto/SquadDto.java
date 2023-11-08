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
    private int totalPay;
    private String maintotalPrice;
    private Map<String, Integer> ovr;
    private List<Player> players;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Player {
        private String spid;
        private String pid;
        private String name;
        private String role;
        private int ovr;
        private int pay;
        private int buildUp;
        private String price;
        private String thumb;
        private double x;
        private double y;
    }
}
