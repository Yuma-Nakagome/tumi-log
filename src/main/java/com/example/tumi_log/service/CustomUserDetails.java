package com.example.tumi_log.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

public class CustomUserDetails extends User {
    @Getter
    private final Long id;

    public CustomUserDetails(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {

        // 親クラス（Spring SecurityのUser）のコンストラクタを呼び出す
        super(username, password, authorities);
        this.id = id; // ユーザーIDを保持
    }
}
