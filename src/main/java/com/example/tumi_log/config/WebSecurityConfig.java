package com.example.tumi_log.config;

import java.io.PrintWriter;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
                                                .requestMatchers("/api/auth/login", "/api/auth/register",
                                                                "/api/auth/me")
                                                .permitAll()

                                                // index.html、JS、CSSはログイン不要でアクセス許可
                                                // "/" (ルートパス)も許可しておくと、ドメイン直下のアクセスがしやすくなります
                                                .requestMatchers("/", "/index.html", "/js/**", "/css/**",
                                                                "/favicon.ico")
                                                .permitAll()

                                                // URLの指定は、その前の requestMatchers() で行います。
                                                // まず API へのアクセス権限を先に定義する
                                                // authenticated() は、「認証が必要」という意味で、アクセスするURLを指定するものではありません。
                                                .requestMatchers("/api/**").authenticated() // APIはログインが必須！

                                                // 上記の2つで定義されなかった他の全てのURLは、permitAll()にする！
                                                // これが非常に重要で、JSルーターに制御を渡すための設定です。
                                                // 例: /editLog/123 のようなパスにアクセスがあっても、ログイン画面にはリダイレクトされず、
                                                // JSが制御できる状態（index.htmlが表示された状態）を維持します。
                                                .anyRequest().permitAll())

                                .exceptionHandling(customizer -> customizer
                                                // 未認証のアクセスに対してはログインページへのリダイレクトではなく、401 Unauthorizedを返す
                                                // これにより、API利用時やSPAで不要なHTMLページが返されるのを防ぐ
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                                .formLogin(form -> form
                                                .loginPage("/login") // ダミーのログインページURLを指定してデフォルト画面生成を抑制
                                                .loginProcessingUrl("/api/auth/login") // JSがPOSTするエンドポイント
                                                .successHandler((request, response, authentication) -> {
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setCharacterEncoding("UTF-8");
                                                        // 成功時は 200 OK とJSONを返す
                                                        response.setStatus(HttpStatus.OK.value());
                                                        PrintWriter writer = response.getWriter(); // Writerの取得はエンコーディング設定後
                                                        writer.write("{\"message\": \"Login successful\"}");
                                                        writer.flush();
                                                })
                                                .failureHandler((request, response, exception) -> {
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setCharacterEncoding("UTF-8");
                                                        // 失敗時は 401 Unauthorized とエラーJSONを返す
                                                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                                        PrintWriter writer = response.getWriter(); // Writerの取得はエンコーディング設定後
                                                        // エラーメッセージの文字列を安全に取得
                                                        String errorMessage = exception.getMessage() != null
                                                                        ? exception.getMessage()
                                                                        : "Authentication failed.";
                                                        writer.write("{\"message\": \"" + errorMessage + "\"}");
                                                        writer.flush();
                                                })
                                                .permitAll() // ログイン処理は誰でも可能
                                )
                                .logout(logout -> logout
                                                .logoutUrl("/api/auth/logout")
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setCharacterEncoding("UTF-8");
                                                        // 成功時は 200 OK とJSONを返す
                                                        response.setStatus(HttpStatus.OK.value());
                                                        PrintWriter writer = response.getWriter(); // Writerの取得はエンコーディング設定後
                                                        writer.write("{\"message\": \"Logout successful\"}");
                                                        writer.flush();
                                                }).permitAll())
                                .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // 5. CORS設定 (異なるオリジンからのアクセスを許可するための設定)
        @Bean
        public CorsFilter corsFilter() {
                // 1. ソースの作成: どのURLパスにCORSルールを適用するかを保持
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                // 2. 設定オブジェクトの作成: 具体的なCORSのルール（誰を許可するか）を定義
                CorsConfiguration config = new CorsConfiguration();
                // 3. 認証情報の許可: 認証情報 (Cookie) の送信を許可
                config.setAllowCredentials(true);
                // 4. 許可するオリジン: 許可リストにURLを追加
                config.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
                // 5. 許可するヘッダー: クライアントが送ってくるヘッダーを許可
                config.addAllowedHeader("*");
                // 6. 許可するメソッド: クライアントが実行できるHTTPメソッドを許可
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                // 7. ルールの登録: "/api/**" のパスに、上記 config のルールを適用
                source.registerCorsConfiguration("/api/**", config);
                // 8. フィルターの返却
                return new CorsFilter(source);
        }
}
