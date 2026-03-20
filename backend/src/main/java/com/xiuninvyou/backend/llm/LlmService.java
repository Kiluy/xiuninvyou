package com.xiuninvyou.backend.llm;

import com.xiuninvyou.backend.model.SystemConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    public String chat(SystemConfig cfg, String userContent) {
        if (cfg.getApiKey() == null || cfg.getApiKey().isBlank()) {
            return "我收到了：" + userContent + "。当前未配置真实 LLM API Key，先使用本地回退回复。";
        }

        String baseUrl = cfg.getApiBaseUrl() == null || cfg.getApiBaseUrl().isBlank()
                ? "https://api.openai.com"
                : cfg.getApiBaseUrl();

        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cfg.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, Object> payload = Map.of(
                "model", cfg.getModelName(),
                "temperature", cfg.getTemperature(),
                "messages", List.of(
                        Map.of("role", "system", "content", cfg.getSystemPrompt() == null ? "" : cfg.getSystemPrompt()),
                        Map.of("role", "user", "content", userContent)
                )
        );

        try {
            Map<?, ?> response = client.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("choices") == null) {
                return "模型返回为空，请检查 API Base URL 与模型名配置。";
            }
            List<?> choices = (List<?>) response.get("choices");
            if (choices.isEmpty()) return "模型没有返回候选结果。";

            Map<?, ?> first = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) first.get("message");
            Object content = message.get("content");
            return content == null ? "模型返回无文本内容。" : content.toString();
        } catch (Exception ex) {
            return "调用真实模型失败：" + ex.getMessage();
        }
    }
}
