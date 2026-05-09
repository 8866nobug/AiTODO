package com.wanger.aitodo.ai.factory;

import com.wanger.aitodo.ai.memory.RedisChatMemoryStore;
import com.wanger.aitodo.ai.service.AiAgentService;
import com.wanger.aitodo.ai.service.AiBaseService;
import com.wanger.aitodo.ai.tool.Tools;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiServiceFactory {

    @Resource
    private AiModelFactory aiModelFactory;



    @Bean
    public ChatMemoryProvider memory(RedisChatMemoryStore redisChatMemoryStore) {
        return memoryId-> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
    }

    /**
     * 实现 1：基础版 AiService
     * 只有模型，没有记忆，没有工具。问过即忘。
     */
    public AiBaseService aiBaseService() {

        return AiServices.builder(AiBaseService.class)
                .chatModel(aiModelFactory.getModel()) // 注意：1.x 中通常使用 chatModel() 方法
                .build();
    }

    /**
     * 实现 2：复杂版 AiService
     * 包含：模型 + 记忆 + 工具 (Tools)
     */
    public AiAgentService aiAgentService(ChatMemoryProvider memory, Tools  tools) {
        return AiServices.builder(AiAgentService.class)
                .chatModel(aiModelFactory.getModel())
                .streamingChatModel(aiModelFactory.getStreamingModel())
                // 记忆：每个 MemoryId 独立，保留最近 10 条消息
                .chatMemoryProvider(memory)
                // 工具：自动扫描 toolObject 中带有 @Tool 注解的方法
                .tools(tools)
                .build();
    }
}
