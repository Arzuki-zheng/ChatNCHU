package com.chatnchu.backend.repository;

import com.chatnchu.backend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // 透過 Email 尋找使用者的神奇方法（Spring Data JPA 會自動實作）
    Optional<AppUser> findByEmail(String email);
}
