package com.wanger.aitodo.ai.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanger.aitodo.mapper.AiConfigMapper;
import com.wanger.aitodo.pojo.aiVO.AiConfig;
import com.wanger.aitodo.pojo.aiVO.AiProviderVO;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AiModelFactory {
    // 动态创建的基本模型
    private ChatModel model;
    //响应式模型
    private StreamingChatModel streamingModel;

    @Resource
    private AiConfigMapper configMapper; // 你的 MyBatis-Plus Mapper

    // 项目启动后自动执行
    @PostConstruct
    public void init() {

        try {
            Integer type =  configMapper.selectOne(new LambdaQueryWrapper<AiConfig>().orderByDesc(AiConfig::getUpdateTime)).getProviderType();
            refreshModel(type);
        } catch (Exception e) {
            log.error("从数据库加载配置失败，使用默认配置", e);
            refreshModel(2); // 提供一个安全的兜底方案
        }
    }

    // 核心方法：从数据库读取并重建模型
    public synchronized void refreshModel(Integer providerType) {
        AiConfig config = configMapper.selectById(providerType);
        if (config != null) {
            this.model = buildModel(config);
            this.streamingModel = buildStreamingModel(config);
        }
    }

    private StreamingChatModel buildStreamingModel(AiConfig config) {
        // 1. 获取厂商枚举
        AiProviderVO provider = AiProviderVO.fromId(config.getProviderType());

        // 2. 获取 API Key 和 URL（如果数据库没填，尝试用枚举里的默认值）
        String apiKey = config.getApiKey();
        String baseUrl = (config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl()
                : provider.getDefaultBaseUrl();

        // 3. 根据厂商类型构建模型
        return switch (provider) {
            case OPENAI ->  OpenAiStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(config.getModelName())
                    .build();

            case QWEN ->  QwenStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(config.getModelName())
                    // Qwen 原生 SDK 有时不需要手动设 baseUrl，除非走中转
                    .build();

            case DEEPSEEK ->  OpenAiStreamingChatModel.builder() // DeepSeek 兼容 OpenAI 格式
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(config.getModelName())
                    .build();

            case OLLAMA -> OllamaStreamingChatModel.builder()
                    .baseUrl(baseUrl)
                    .modelName(config.getModelName())
                    .build();
        };
    }

    private ChatModel buildModel(AiConfig config) {
        // 1. 获取厂商枚举
        AiProviderVO provider = AiProviderVO.fromId(config.getProviderType());

        // 2. 获取 API Key 和 URL（如果数据库没填，尝试用枚举里的默认值）
        String apiKey = config.getApiKey();
        String baseUrl = (config.getBaseUrl() != null && !config.getBaseUrl().isEmpty())
                ? config.getBaseUrl()
                : provider.getDefaultBaseUrl();

        // 3. 根据厂商类型构建模型
        return switch (provider) {
            case OPENAI -> OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(config.getModelName())
                    .build();

            case QWEN -> QwenChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(config.getModelName())
                    // Qwen 原生 SDK 有时不需要手动设 baseUrl，除非走中转
                    .build();

            case DEEPSEEK -> OpenAiChatModel.builder() // DeepSeek 兼容 OpenAI 格式
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .modelName(config.getModelName())
                    .build();

            case OLLAMA -> OllamaChatModel.builder()
                    .baseUrl(baseUrl)
                    .modelName(config.getModelName())
                    .build();
        };
    }


    public ChatModel getModel() {
        return model;
    }

    public StreamingChatModel getStreamingModel() {
        return streamingModel;
    }
}
