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

                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**") // ★ APIパス全体でCSRF保護を無効化
                                )
                                .authorizeHttpRequests(authz -> authz
                                                // "/api/**" の制限よりも「前に」書くことで、ここだけ穴を開けて通します。
                                                .requestMatchers("/api/auth/**", "/api/log").permitAll()
                                                // authenticated() は、「認証が必要」という意味で、アクセスするURLを指定するものではありません。
                                                // URLの指定は、その前の requestMatchers() で行います。
                                                // まず API へのアクセス権限を先に定義する
                                                .requestMatchers("/api/**").authenticated() // APIはログインが必須！

                                                // index.html、JS、CSSはログイン不要でアクセス許可
                                                // "/" (ルートパス)も許可しておくと、ドメイン直下のアクセスがしやすくなります
                                                .requestMatchers("/", "/index.html", "/js/**", "/css/**",
                                                                "/favicon.ico")
                                                .permitAll()

                                                // 上記の2つで定義されなかった他の全てのURLは、permitAll()にする！
                                                // これが非常に重要で、JSルーターに制御を渡すための設定です。
                                                // 例: /editLog/123 のようなパスにアクセスがあっても、ログイン画面にはリダイレクトされず、
                                                // JSが制御できる状態（index.htmlが表示された状態）を維持します。
                                                .anyRequest().permitAll())
                                .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
