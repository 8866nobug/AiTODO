package com.wanger.aitodo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /files/ 路径映射到本地磁盘
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:D:/ai-todo/exports/");
    }
}
