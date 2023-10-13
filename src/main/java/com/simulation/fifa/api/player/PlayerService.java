package com.simulation.fifa.api.player;

import com.simulation.fifa.api.player.dto.PlayerDetailDto;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.dto.PlayerSearchDto;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    PlayerRepository playerRepository;

    public Page<PlayerListDto> findAll(Pageable pageable, PlayerSearchDto playerSearchDto) {
        return playerRepository.findAllCustom(pageable, playerSearchDto);
    }

    public PlayerDetailDto findById(Long id) {
        return playerRepository.findByIdCustom(id).orElseThrow(() -> new UsernameNotFoundException("해당 선수를 찾을수 없습니다."));
    }
}
