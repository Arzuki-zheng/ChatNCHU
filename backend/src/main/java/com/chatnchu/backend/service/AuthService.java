package com.chatnchu.backend.service;

import com.chatnchu.backend.entity.AppUser;
import com.chatnchu.backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private AppUserRepository userRepository;

    public Map<String, Object> loginOrRegister(Jwt jwt) {
        // 從 Google Token 裡抓資料
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        // 檢查資料庫有沒有這個人，沒有就建立新的
        AppUser user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    AppUser newUser = AppUser.builder()
                            .email(email)
                            .name(name)
                            .build();
                    return userRepository.save(newUser);
                });

        // 回傳給前端的資料
        return Map.of(
                "message", "Login successful",
                "userId", user.getId(),
                "email", user.getEmail(),
                "name", user.getName()
        );
    }
}
