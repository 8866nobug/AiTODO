package com.wanger.aitodo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册连接点，前端通过 ws://localhost:8080/ws-agent 建立连接
        registry.addEndpoint("/ws-agent")
                .setAllowedOriginPatterns("*") // 开发阶段允许跨域
                .withSockJS(); // 增强兼容性
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置消息代理前缀，前端订阅 /topic/... 即可收到消息
        registry.enableSimpleBroker("/topic");
        // 服务端接收消息的前缀（本项目主要由后端推，暂不深入）
        registry.setApplicationDestinationPrefixes("/app");
    }
}