package com.simulation.fifa.api.user.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.user.dto.squad.SquadDto;
import com.simulation.fifa.api.user.dto.UserDto;
import com.simulation.fifa.api.user.dto.match.UserMatchDetailDto;
import com.simulation.fifa.api.user.dto.match.UserMatchDto;
import com.simulation.fifa.api.user.dto.match.UserMatchRequestDto;
import com.simulation.fifa.api.user.dto.trade.UserTradeListDto;
import com.simulation.fifa.api.user.dto.trade.UserTradeRequestDto;
import com.simulation.fifa.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> findUserInfo(@RequestParam String nickname) {
        UserDto userInfo = userService.findUserInfo(nickname);
        return ResponseEntity.ok(ApiResponse.createSuccess(userInfo));
    }

    @GetMapping("/trades")
    public ResponseEntity<ApiResponse<?>> findUserTrades(@RequestParam String nickname, UserTradeRequestDto userTradeRequestDto) {
        List<UserTradeListDto> userTradeList = userService.findUserTrades(nickname, userTradeRequestDto);
        return ResponseEntity.ok(ApiResponse.createSuccess(userTradeList));
    }

    @GetMapping("/squad")
    public ResponseEntity<ApiResponse<?>> findUserSquad_new(@RequestParam String nickname) {
        SquadDto squadDto = userService.findUserSquad(nickname);

        return ResponseEntity.ok(ApiResponse.createSuccess(squadDto));
    }

    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<?>> findUserMatches(@RequestParam String nickname, UserMatchRequestDto userMatchRequestDto) {
        List<UserMatchDto> matchList = userService.findUserMatchList(nickname, userMatchRequestDto);
        return ResponseEntity.ok(ApiResponse.createSuccess(matchList));
    }

    @GetMapping("/matches/{matchId}")
    public ResponseEntity<ApiResponse<?>> findUserMatches(@PathVariable String matchId) {
        UserMatchDetailDto matchList = userService.findUserMatchByMatchId(matchId);
        return ResponseEntity.ok(ApiResponse.createSuccess(matchList));
    }

}
