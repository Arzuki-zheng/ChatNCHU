package com.chatnchu.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RagRequest {

    // 必填欄位
    private String query;

    // 選填欄位，用 @Builder.Default 設定預設值
    // 使用 @JsonProperty 確保轉成 JSON 時欄位名稱正確 (snake_case)
    @Builder.Default
    @JsonProperty("top_k")
    private int topK = 5;

    @Builder.Default
    private double temperature = 0.1;
}
