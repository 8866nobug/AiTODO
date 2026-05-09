package com.wanger.aitodo.service.impl;

import com.wanger.aitodo.ai.service.AiAgentService;
import com.wanger.aitodo.service.AgentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AgentServiceImpl implements AgentService {

    @Resource
    private AiAgentService aiAgentService;


    @Override
    public Flux<String> chat(String prompt) {
         return aiAgentService.chatWithAgent(prompt);
    }
}
