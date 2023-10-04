package com.simulation.fifa.api.player.service;

import com.simulation.fifa.api.player.dto.SpIdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PlayerService {
    @Value("${nexon.fifa-online.static-api-url}")
    private String staticApiUrl;

    @Autowired
    WebClient webClient;

    public List<SpIdDto> getPlayerSpidList() {
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
                .bodyToMono(new ParameterizedTypeReference<List<SpIdDto>>(){})
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .block();
    }


}
