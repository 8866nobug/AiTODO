package com.wanger.aitodo.ai.service;


import com.wanger.aitodo.ai.guardrail.SafeInputGuardrail;
import com.wanger.aitodo.pojo.aiVO.AiTaskFlowVO;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;

@InputGuardrails({SafeInputGuardrail.class})
public interface AiBaseService {

    @SystemMessage(fromResource = "static/system-prompt-task-flow.txt")
    AiTaskFlowVO analyseTaskFlow(@UserMessage String userMessage);
}
