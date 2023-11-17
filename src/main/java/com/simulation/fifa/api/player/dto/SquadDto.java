package com.simulation.fifa.api.player.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;
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
    private List<TotalPrice> totalPriceList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Player {
        private long spid;
        private long pid;
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

    @Data
    public static class TotalPrice {
        private long totalPrice;
        private LocalDate date;

        @QueryProjection
        public TotalPrice(long totalPrice, LocalDate date) {
            this.totalPrice = totalPrice;
            this.date = date;
        }
    }

    public void updateTotalPrice(List<TotalPrice> totalPriceList) {
        this.totalPriceList = totalPriceList;
    }

}
