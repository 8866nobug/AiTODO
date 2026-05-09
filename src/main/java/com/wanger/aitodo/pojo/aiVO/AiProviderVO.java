package com.wanger.aitodo.pojo.aiVO;

import lombok.Getter;

@Getter
public enum AiProviderVO {
    OPENAI(1, "OpenAI", "https://api.openai.com/v1"),
    QWEN(2, "通义千问", "https://dashscope.aliyuncs.com/api/v1"),
    DEEPSEEK(3, "DeepSeek", "https://api.deepseek.com"),
    OLLAMA(4, "Ollama", "http://localhost:11434/v1");

    private final int id;
    private final String name;
    private final String defaultBaseUrl;

    AiProviderVO(int id, String name, String defaultBaseUrl) {
        this.id = id;
        this.name = name;
        this.defaultBaseUrl = defaultBaseUrl;
    }

    /**
     * 根据数据库中的 ID 获取枚举对象
     */
    public static AiProviderVO fromId(Integer id) {
        if (id == null) return QWEN; // 默认返回 Qwen
        for (AiProviderVO provider : values()) {
            if (provider.id == id) {
                return provider;
            }
        }
        return QWEN; // 匹配不到时返回默认值
    }
}
