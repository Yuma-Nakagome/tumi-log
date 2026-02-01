package com.example.tumi_log.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tumi_log.service.CustomUserDetails;
import com.example.tumi_log.dto.UserMeDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/me")
    public ResponseEntity<UserMeDto> me(@AuthenticationPrincipal CustomUserDetails principal) {
        if (null == principal) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = principal.getId();
        String username = principal.getUsername();
        UserMeDto userMeDto = new UserMeDto(userId, username);
        return ResponseEntity.status(HttpStatus.OK).body(userMeDto);
    }

}
