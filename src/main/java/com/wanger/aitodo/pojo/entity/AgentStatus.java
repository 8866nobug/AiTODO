package com.wanger.aitodo.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentStatus {

    public enum Status {
        // 1. 定义常量实例，必须写在最前面
        THINKING(100, "AI 正在思考"),
        ACTING(200, "正在调用工具"),
        SUCCESS(300, "任务完成"),
        FAILED(500, "执行失败");

        // 2. 定义成员变量
        private final int code;
        private final String description;

        // 3. 构造函数（必须是私有的，默认就是 private）
        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        // 4. 定义 Getter 方法
        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    private String requestId; // 关联当前的 SSE 请求
    private Status state;     // THINKING, ACTING, FILE_READY, COMPLETED
    private String message;   // 具体描述（如：正在生成PDF）
    private Object payload;   // 携带的数据（如：文件下载 URL）

    public static AgentStatus of(Status state, String message, Object payload) {
        return new AgentStatus(null, state, message, payload);
    }
}

