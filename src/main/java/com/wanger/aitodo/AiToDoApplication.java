package com.wanger.aitodo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan ("com.wanger.aitodo.mapper")
@EnableScheduling
public class AiToDoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiToDoApplication.class, args);
    }

}
