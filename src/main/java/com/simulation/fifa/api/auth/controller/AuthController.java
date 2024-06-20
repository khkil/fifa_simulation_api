package com.simulation.fifa.api.auth.controller;

import com.simulation.fifa.api.auth.service.AuthKakaoService;
import com.simulation.fifa.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/auth"))
@RequiredArgsConstructor
public class AuthController {
    private final AuthKakaoService authKakaoService;

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<?>> kakaoLogin(@RequestParam String code) {
        authKakaoService.kakaoLogin(code);
        return null;
    }
}
