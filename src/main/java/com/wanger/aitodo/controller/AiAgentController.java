package com.wanger.aitodo.controller;

import com.wanger.aitodo.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/agent")
@CrossOrigin(origins = "*")
public class AiAgentController {

    @Resource
    private AgentService agentService;

    @Operation(summary = "与AI代理进行对话", description = "用户输入自然语言指令，AI代理将根据上下文和工具执行相应操作")
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestParam String prompt) {

        return agentService.chat(prompt)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());

    }
}
