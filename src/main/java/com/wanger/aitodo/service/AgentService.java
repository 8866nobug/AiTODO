package com.wanger.aitodo.service;

import reactor.core.publisher.Flux;

public interface AgentService {
    Flux<String> chat(String prompt);
}
