package com.chatnchu.backend.controller;

import com.chatnchu.backend.dto.ApiResponse;
import com.chatnchu.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 前端把 Token 放在 Header 帶過來，後端自動解析成 Jwt 物件
    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> googleLogin(@AuthenticationPrincipal Jwt jwt) {
        try {
            Map<String, Object> userData = authService.loginOrRegister(jwt);
            // HTTP 200 + Body
            return ResponseEntity.ok(ApiResponse.success(200, "登入成功", userData));
        } catch (Exception e) {
            // 範例：如果驗證失敗
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "登入失敗：" + e.getMessage()));
        }
    }

}
