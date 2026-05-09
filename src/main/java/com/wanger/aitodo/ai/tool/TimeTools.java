package com.wanger.aitodo.ai.tool;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeTools {

    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }
}