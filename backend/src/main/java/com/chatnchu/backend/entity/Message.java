package com.chatnchu.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT") // 內容可能會很長，用 TEXT 格式
    private String content;

    @Enumerated(EnumType.STRING)
    private Role role; // 是 USER 講的還是 ASSISTANT (AI) 講的

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    @JsonIgnore
    private Conversation conversation; // 屬於哪個對話串

    public enum Role {
        USER, ASSISTANT, SYSTEM
    }
}
