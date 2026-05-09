package com.wanger.aitodo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanger.aitodo.pojo.aiVO.AiConfig;

public interface AiConfigService extends IService<AiConfig> {
    void update(AiConfig aiConfig);
}
