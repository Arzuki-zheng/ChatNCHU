package com.chatnchu.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 關閉 CSRF (關鍵！API 必關)
                .csrf(AbstractHttpConfigurer::disable)

                // 設定路由權限
                .authorizeHttpRequests(auth -> auth
                        // 允許這幾個路徑不用登入就能跑 (包含登入API、H2 Console、錯誤頁面)
                        .requestMatchers("/api/auth/**", "/error", "/h2-console/**").permitAll()
                        // 其他所有 API 都要有 JWT 才能用
                        .anyRequest().authenticated()
                )

                // 啟用 JWT 驗證功能
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }
}
