package com.chatnchu.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    // 雖然 HTTP Header 已經有 Status Code，但有些前端框架喜歡在 Body 再確認一次
    private int status;
    private String mes;
    private T data;

    // 成功時的回應 (通常用這個)
    public static <T> ApiResponse<T> success(int status, String mes, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .mes(mes)
                .data(data)
                .build();
    }

    // 錯誤時的回應 (data 通常是 null，或包含錯誤細節)
    public static <T> ApiResponse<T> error(int status, String mes) {
        return ApiResponse.<T>builder()
                .status(status)
                .mes(mes)
                .data(null)
                .build();
    }
}
