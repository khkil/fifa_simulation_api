package com.simulation.fifa.api.user.controller;

import com.simulation.fifa.api.common.ApiResponse;
import com.simulation.fifa.api.user.dto.UserTradeListDto;
import com.simulation.fifa.api.user.dto.UserTradeRequestDto;
import com.simulation.fifa.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/trades")
    public ResponseEntity<ApiResponse<?>> getUserTradeInfo(@RequestParam String nickname, UserTradeRequestDto userTradeRequestDto) {
        List<UserTradeListDto> userTradeList = userService.findAllTradeList(nickname, userTradeRequestDto);
        return ResponseEntity.ok(ApiResponse.createSuccess(userTradeList));
    }
}
