package com.example.tumi_log.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
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
                .map(u -> User.withUsername(u.getUserName())
                        .password(u.getPasswordHash()) // DBのハッシュを返す（例: $2a$...）
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }
}
