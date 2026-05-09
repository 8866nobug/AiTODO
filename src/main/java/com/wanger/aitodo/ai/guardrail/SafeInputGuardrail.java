package com.wanger.aitodo.ai.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;

import java.util.HashSet;
import java.util.Set;


public class SafeInputGuardrail implements InputGuardrail {

    private static final Set<String> sensitiveWords = new HashSet<String>();
    static {
        // 在这里添加敏感词，可以从配置文件或数据库加载
        sensitiveWords.add("死亡");
        sensitiveWords.add("自杀");
        sensitiveWords.add("强奸");
    }

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String input = userMessage.singleText();
        if(!invalidContained(input)) { // 这里设置输入长度限制为100
            return  failure("输入包含敏感词或长度超过限制，请重新输入");
        }
        return success();
    }


    private boolean invalidContained(String input) {
         for (String sensitiveWord : sensitiveWords) {
             if (input.contains(sensitiveWord)) {
                 return false;
             }
         }
         return true;
    }




}
