package com.wanger.aitodo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanger.aitodo.mapper.AiConfigMapper;
import com.wanger.aitodo.pojo.aiVO.AiConfig;
import com.wanger.aitodo.service.AiConfigService;
import org.springframework.stereotype.Service;

@Service
public class AiConfigServiceImpl extends ServiceImpl<AiConfigMapper,AiConfig> implements AiConfigService {

    @Override
    public void update(AiConfig aiConfig) {
        updateById(aiConfig);
    }
}
