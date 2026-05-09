package com.wanger.aitodo.controller;

import com.wanger.aitodo.mapper.AiConfigMapper;
import com.wanger.aitodo.pojo.aiVO.AiConfig;
import com.wanger.aitodo.pojo.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config" )
public class ConfigController {

    @Resource
    private AiConfigMapper aiConfigMapper;

    @Operation(summary = "大模型配置",description = "厂商型号以及api")
    @PostMapping("/model")
    public Result updateModel(AiConfig aiConfig) {
        aiConfigMapper.updateById(aiConfig);
        return Result.success();
    }
}
