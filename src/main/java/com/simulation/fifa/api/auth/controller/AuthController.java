package com.simulation.fifa.api.auth.controller;

import com.simulation.fifa.api.auth.dto.KakaoLoginRequestDto;
import com.simulation.fifa.api.auth.dto.KakaoLoginResponseDto;
import com.simulation.fifa.api.auth.service.OAuth2Service;
import com.simulation.fifa.api.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(("/api/auth"))
public class AuthController {
    @Qualifier("kakao")
    @Autowired
    private OAuth2Service<KakaoLoginResponseDto, KakaoLoginRequestDto> kakaoService;

    /*@Qualifier("google")
    @Autowired
    private OAuth2Service googleService;*/

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<?>> kakaoLogin(@RequestBody KakaoLoginRequestDto kakaoLoginRequestDto) {
        KakaoLoginResponseDto kakaoLoginResponseDto = kakaoService.login(kakaoLoginRequestDto);
        return null;
    }
}
