package com.chatnchu.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class RagResponse {

    private String answer;

    private List<SourceDocument> sources;

    @JsonProperty("processing_time") // 對應 Python 的 snake_case
    private Double processingTime;   // 使用 Double 物件型別，因為 Python 那邊可能是 null

    @Data
    @NoArgsConstructor
    public static class SourceDocument {
        private String filename;
    }
}
