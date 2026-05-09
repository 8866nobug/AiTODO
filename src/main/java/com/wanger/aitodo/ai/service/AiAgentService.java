package com.wanger.aitodo.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiAgentService {

    @SystemMessage(fromResource = "static/system-prompt-agent.txt")
    Flux<String> chatWithAgent(@UserMessage String userMessage);
}
