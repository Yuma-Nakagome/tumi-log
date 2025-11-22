package com.example.tumi_log.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/registerUser", "/login")
                                                .permitAll()
                                                .anyRequest().authenticated())

                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .failureUrl("/login?error")
                                                .defaultSuccessUrl("/dashboard", true))

                                .logout(logout -> logout
                                                .logoutUrl("/logout") // ログアウト実行URL
                                                .logoutSuccessUrl("/login?logout") // ★ 成功後、メッセージ付きでログイン画面へ戻る
                                                .permitAll() // ログアウト処理は誰でも実行OK
                                )
                                .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
