package com.simulation.fifa.api.player;

import com.simulation.fifa.api.player.dto.PlayerDetailDto;
import com.simulation.fifa.api.player.dto.PlayerListDto;
import com.simulation.fifa.api.player.dto.PlayerSearchDto;
import com.simulation.fifa.api.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public Page<PlayerListDto> findAll(Pageable pageable, PlayerSearchDto playerSearchDto) {
        return playerRepository.findAllCustom(pageable, playerSearchDto);
    }

    public PlayerDetailDto findById(Long id) {
        return playerRepository.findByIdCustom(id).orElseThrow(() -> new UsernameNotFoundException("해당 선수를 찾을수 없습니다."));
    }
}
