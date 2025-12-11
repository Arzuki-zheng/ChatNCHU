package com.chatnchu.backend.controller;

import com.chatnchu.backend.dto.ApiResponse;
import com.chatnchu.backend.entity.AppUser;
import com.chatnchu.backend.entity.Conversation;
import com.chatnchu.backend.entity.Message;
import com.chatnchu.backend.repository.AppUserRepository;
import com.chatnchu.backend.repository.ConversationRepository;
import com.chatnchu.backend.repository.MessageRepository;
import com.chatnchu.backend.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    // 1. 補上 Repositories 的依賴注入
    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AiService aiService;

    // 2. 開啟新對話
    @PostMapping("/new")
    public ResponseEntity<ApiResponse<Conversation>> createConversation(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> body) {

        String email = jwt.getClaimAsString("email");
        AppUser user = userRepository.findByEmail(email).orElseThrow();

        Conversation conversation = Conversation.builder()
                .title(body.getOrDefault("title", "New Chat"))
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Conversation saved = conversationRepository.save(conversation);

        return ResponseEntity.ok(ApiResponse.success(200, "開啟新對話成功", saved));
    }

    // 3. 發送訊息 (補齊邏輯)
    @PostMapping("/{conversationId}/message")
    public ResponseEntity<ApiResponse<List<Message>>> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody Map<String, String> body) {

        if (!conversationRepository.existsById(conversationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "找不到該對話框"));
        }

        Conversation conversation = conversationRepository.findById(conversationId).get();

        // 3-1. 儲存使用者訊息
        String userContent = body.get("content"); // 拿出來比較乾淨
        Message userMessage = Message.builder()
                .content(userContent)
                .role(Message.Role.USER)
                .createdAt(LocalDateTime.now())
                .conversation(conversation)
                .build();
        messageRepository.save(userMessage);

        // 3-2. 呼叫 Python RAG 取得真實回應！
        // 這裡會稍微卡住一下(Block)，直到 Python 回傳，這在簡單架構下是 OK 的
        String aiResponseText = aiService.getAnswerFromRAG(userContent);

        Message aiMessage = Message.builder()
                .content(aiResponseText)
                .role(Message.Role.ASSISTANT)
                .createdAt(LocalDateTime.now().plusSeconds(1))
                .conversation(conversation)
                .build();
        messageRepository.save(aiMessage);

        List<Message> result = List.of(userMessage, aiMessage);
        return ResponseEntity.ok(ApiResponse.success(200, "訊息發送成功", result));
    }

    // 4. 取得歷史紀錄 (補上這個方法)
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<Message>>> getMessages(@PathVariable Long conversationId) {
        // 這裡假設你有寫 findByConversationIdOrderByCreatedAtAsc，如果沒有可以使用 findAll
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        return ResponseEntity.ok(ApiResponse.success(200, "取得歷史紀錄成功", messages));
    }

    // 5. 取得使用者對話列表 (補上這個方法)
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Conversation>>> getUserConversations(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        AppUser user = userRepository.findByEmail(email).orElseThrow();

        List<Conversation> list = conversationRepository.findByUserId(user.getId());

        // 簡單排序：新的在前面
        list.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        return ResponseEntity.ok(ApiResponse.success(200, "取得對話列表成功", list));
    }

    // 6. 刪除對話
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        AppUser user = userRepository.findByEmail(email).orElseThrow();

        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);

        if (conversation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "對話不存在"));
        }

        if (!conversation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, "你沒有權限刪除此對話"));
        }

        conversationRepository.delete(conversation);
        return ResponseEntity.ok(ApiResponse.success(200, "刪除成功", null));
    }
}
