package com.chatnchu.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 標記為配置類別，Spring Boot 啟動時會自動載入
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // 替換這裡的 URL 為您前端服務實際運行的地址
        // 如果您的前端在本地運行，通常是 http://localhost:5173
        // 如果您將前端部署在其他地方，請使用那個公開 URL
        String frontendOrigin = "http://localhost:5173";

        registry.addMapping("/**") // 應用於所有的 API 路徑 (/**)
                .allowedOrigins(frontendOrigin) // 允許來自前端的地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許所有標準 HTTP 方法
                .allowedHeaders("Authorization", "Content-Type") // **關鍵：允許攜帶認證 Header**
                .allowCredentials(true) // 允許攜帶認證資訊 (如 Cookies)
                .maxAge(3600); // 預檢請求的緩存時間 (1 小時)
    }
}