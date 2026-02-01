package com.example.tumi_log.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.example.tumi_log.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
                .map(user -> {
                    // 権限のリスト（今回は "USER" ロールのみでOK）を作成
                    List<GrantedAuthority> authorities = Collections
                            .singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    return new CustomUserDetails(
                            user.getId(), // ★ IDを渡す (新しい CustomUserDetails の引数に追加したもの)
                            user.getUserName(), // ユーザー名
                            user.getPasswordHash(), // DBのハッシュパスワード
                            authorities // 権限リスト
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }
}
