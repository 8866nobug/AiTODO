package com.wanger.aitodo.ai.tool;

import com.wanger.aitodo.pojo.aiVO.AiDateTimeVO;
import com.wanger.aitodo.pojo.entity.AgentStatus;
import com.wanger.aitodo.pojo.entity.Task;
import com.wanger.aitodo.service.TaskService;
import dev.langchain4j.agent.tool.P;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class QueryTaskTools {

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    @Resource
    private TaskService taskService;


    public String queryHistoryTasks(
             Boolean status,
             Boolean isImportant,
             AiDateTimeVO aiDateTimeVO) {

        wsTemplate.convertAndSend("/topic/agent/status", AgentStatus.of(AgentStatus.Status.ACTING,"\uD83D\uDE80 正在查询历史任务",null));


        // 1. 安全处理：如果 VO 为空，初始化一个
        if (aiDateTimeVO == null) {
            aiDateTimeVO = new AiDateTimeVO();
        }

        LocalDateTime now = LocalDateTime.now();

        // 2. 使用 Optional 优雅处理可能出现的 null，防止 NPE
        LocalDateTime startTime = now
                .minusMinutes(Optional.ofNullable(aiDateTimeVO.getMinute()).orElse(0))
                .minusHours(Optional.ofNullable(aiDateTimeVO.getHour()).orElse(0))
                .minusDays(Optional.ofNullable(aiDateTimeVO.getDay()).orElse(0))
                .minusMonths(Optional.ofNullable(aiDateTimeVO.getMonth()).orElse(0))
                .minusYears(Optional.ofNullable(aiDateTimeVO.getYear()).orElse(0));

        // 3. 动态 SQL 优化：只有当参数不为 null 时才加入查询条件
        List<Task> list = taskService.lambdaQuery()
                .eq(status != null, Task::getStatus, status)
                .eq(isImportant != null, Task::getIsImportant, isImportant)
                .between(Task::getCreateTime, startTime, now)
                .list();

        // 4. 空结果处理
        if (list.isEmpty()) {
            return "在指定的时间范围内没有找到符合条件的任务。";
        }

        return list.stream()
                .map(t -> String.format("- 任务: %s, 状态: %s, 重要性: %s, 创建时间: %s, 提醒时间: %s",
                        t.getTitle(),
                        t.getStatus() ? "已完成" : "未完成",
                        t.getIsImportant() ? "高" : "普通",
                        t.getCreateTime(),
                        t.getRemindTime() != null ? t.getRemindTime() : "无"))
                .collect(Collectors.joining("\n"));
    }


    public String queryFutureTasks(
            Boolean status,
            Boolean isImportant,
            AiDateTimeVO aiDateTimeVO) {

        wsTemplate.convertAndSend("/topic/agent/status",AgentStatus.of(AgentStatus.Status.ACTING,"\uD83D\uDE80 正在查询未来任务",null));


        // 1. 安全处理：如果 VO 为空，初始化一个
        if (aiDateTimeVO == null) {
            aiDateTimeVO = new AiDateTimeVO();
        }

        LocalDateTime now = LocalDateTime.now();

        // 2. 使用 Optional 优雅处理可能出现的 null，防止 NPE
        LocalDateTime startTime = now
                .plusMinutes(Optional.ofNullable(aiDateTimeVO.getMinute()).orElse(0))
                .plusHours(Optional.ofNullable(aiDateTimeVO.getHour()).orElse(0))
                .plusDays(Optional.ofNullable(aiDateTimeVO.getDay()).orElse(0))
                .plusMonths(Optional.ofNullable(aiDateTimeVO.getMonth()).orElse(0))
                .plusYears(Optional.ofNullable(aiDateTimeVO.getYear()).orElse(0));

        // 3. 动态 SQL 优化：只有当参数不为 null 时才加入查询条件
        List<Task> list = taskService.lambdaQuery()
                .eq(status != null, Task::getStatus, status)
                .eq(isImportant != null, Task::getIsImportant, isImportant)
                .between(Task::getRemindTime, now,startTime)
                .list();

        // 4. 空结果处理
        if (list.isEmpty()) {
            return "在指定的时间范围内没有找到符合条件的任务。";
        }

        return list.stream()
                .map(t -> String.format("- 任务: %s, 状态: %s, 重要性: %s, 创建时间: %s, 提醒时间: %s",
                        t.getTitle(),
                        t.getStatus() ? "已完成" : "未完成",
                        t.getIsImportant() ? "高" : "普通",
                        t.getCreateTime(),
                        t.getRemindTime() != null ? t.getRemindTime() : "无"))
                .collect(Collectors.joining("\n"));
    }






}
