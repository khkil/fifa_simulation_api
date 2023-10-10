package com.simulation.fifa.api.player;

import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.entity.Player;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    @Autowired
    PlayerRepository playerRepository;

    public Page<PlayerListDto> findAll(Pageable pageable) {
        return playerRepository.findAllCustom(pageable);
    }
}
