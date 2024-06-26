package com.simulation.fifa.api.platform.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.platform.dto.squad.SquadDto;
import com.simulation.fifa.api.platform.dto.UserDto;
import com.simulation.fifa.api.platform.dto.match.UserMatchDetailDto;
import com.simulation.fifa.api.platform.dto.match.UserMatchDto;
import com.simulation.fifa.api.platform.dto.match.UserMatchRequestDto;
import com.simulation.fifa.api.platform.dto.trade.UserTradeListDto;
import com.simulation.fifa.api.platform.dto.trade.UserTradeRequestDto;
import com.simulation.fifa.api.platform.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class PlatformController {
    private final PlatformService platformService;

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<?>> findUserInfo(@RequestParam String nickname) {
        UserDto userInfo = platformService.findUserInfo(nickname);
        return ResponseEntity.ok(ApiResponse.createSuccess(userInfo));
    }

    @GetMapping("/trades")
    public ResponseEntity<ApiResponse<?>> findUserTrades(@RequestParam String nickname, UserTradeRequestDto userTradeRequestDto) {
        List<UserTradeListDto> userTradeList = platformService.findUserTrades(nickname, userTradeRequestDto);
        return ResponseEntity.ok(ApiResponse.createSuccess(userTradeList));
    }

    @GetMapping("/squad")
    public ResponseEntity<ApiResponse<?>> findUserSquad(@RequestParam String nickname) {
        SquadDto squadDto = platformService.findUserSquad(nickname);

        return ResponseEntity.ok(ApiResponse.createSuccess(squadDto));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<?>> findUserMatches(@RequestParam String nickname, UserMatchRequestDto userMatchRequestDto) {
        List<UserMatchDto> matchList = platformService.findUserMatchList(nickname, userMatchRequestDto);
        return ResponseEntity.ok(ApiResponse.createSuccess(matchList));
    }

    @GetMapping("/matches/{matchId}")
    public ResponseEntity<ApiResponse<?>> findUserMatchDetail(@PathVariable String matchId) {
        UserMatchDetailDto matchList = platformService.findUserMatchDetail(matchId);
        return ResponseEntity.ok(ApiResponse.createSuccess(matchList));
    }

}
