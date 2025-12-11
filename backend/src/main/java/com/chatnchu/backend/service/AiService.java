package com.chatnchu.backend.service;

import com.chatnchu.backend.dto.RagRequest;
import com.chatnchu.backend.dto.RagResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    // 從設定檔讀取 Python API 網址，預設為 localhost:5000
    @Value("${app.ai.api-url:http://localhost:5000}")
    private String pythonApiUrl;

    private final RestTemplate restTemplate;

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    public String getAnswerFromRAG(String userMessage) {
        try {
            String endpoint = pythonApiUrl + "/chat";

            RagRequest request = RagRequest.builder()
                    .query(userMessage)
                    .topK(5)            // 改成跟 Python Swagger 預設一樣
                    .temperature(0.1)   // 改成跟 Python Swagger 預設一樣
                    .build();

            // 2. 發送 POST 請求
            RagResponse response = restTemplate.postForObject(endpoint, request, RagResponse.class);

            // 3. 處理回應
            if (response != null && response.getAnswer() != null) {
                // 這裡可以選擇是否要把「參考來源」也加進回覆文字裡
                // 例如： "AI回答.... \n\n(參考資料: doc1.pdf)"
                return formatResponse(response);
            } else {
                return "抱歉，AI 暫時無法回應 (Empty Response)。";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，目前 AI 服務連線異常，請稍後再試。(" + e.getMessage() + ")";
        }
    }

    // 格式化輸出：把參考文件加在後面 (Optional)
    private String formatResponse(RagResponse response) {
        StringBuilder sb = new StringBuilder(response.getAnswer());

        if (response.getSources() != null && !response.getSources().isEmpty()) {
            sb.append("\n\n---\n參考來源：\n");
            for (RagResponse.SourceDocument doc : response.getSources()) {
                sb.append("- ").append(doc.getFilename()).append("\n");
            }
        }
        return sb.toString();
    }
}
