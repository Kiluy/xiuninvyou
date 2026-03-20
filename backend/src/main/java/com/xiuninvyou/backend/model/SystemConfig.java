package com.xiuninvyou.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_config")
public class SystemConfig {
    @Id
    private Long id = 1L;

    @Column(name = "model_provider", nullable = false)
    private String modelProvider = "deepseek";

    @Column(name = "model_name", nullable = false)
    private String modelName = "deepseek-chat";

    @Column(name = "temperature", nullable = false)
    private Double temperature = 0.8;

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt = "你是一个温柔、长期陪伴型的 AI 虚拟伴侣。";

    @Column(name = "api_base_url")
    private String apiBaseUrl = "https://api.openai.com";

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "admin_command", nullable = false)
    private String adminCommand = "/sudo-admin-777";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getModelProvider() { return modelProvider; }
    public void setModelProvider(String modelProvider) { this.modelProvider = modelProvider; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getAdminCommand() { return adminCommand; }
    public void setAdminCommand(String adminCommand) { this.adminCommand = adminCommand; }
}
