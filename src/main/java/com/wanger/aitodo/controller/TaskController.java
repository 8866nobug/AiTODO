package com.wanger.aitodo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wanger.aitodo.pojo.dto.TaskDTO;
import com.wanger.aitodo.pojo.dto.UserInputDTO;
import com.wanger.aitodo.pojo.entity.Task;
import com.wanger.aitodo.pojo.result.Result;
import com.wanger.aitodo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(name="任务模块", description="处理Todo任务的增删改查")
public class TaskController {

    @Resource
    private TaskService taskService;

    @Operation(summary = "添加新任务",description = "用户输入原始指令，AI 将自动进行语义明确化,并添加")
    @PostMapping("/add")
    public Result add(@RequestBody UserInputDTO  userInputDTO) {

        taskService.saveTask(userInputDTO);
        return Result.success();
    }

    @Operation(summary = "删除任务",description = "将任务从数据库中删除包括关联关系")
    @DeleteMapping("/delect/{taskId}/{categoryId}")
    public Result delect(@PathVariable Long taskId, @PathVariable Long categoryId) {
        taskService.deleteTask(taskId,categoryId);
        return Result.success();
    }

    @Operation(summary = "更新任务",description = "用户修改任务信息，ai可用")
    @PutMapping("/update")
    public Result update(@RequestBody TaskDTO taskDTO) {
        taskService.updateTask(taskDTO);
        return Result.success();
    }

    @Operation(summary = "查看任务详情",description = "根据任务ID查看任务详情,ai可用")
    @GetMapping("/get/{taskId}")
    public Result get(@PathVariable Long taskId) {
        return Result.success(taskService.getById(taskId));
    }

    @Operation(summary ="根据分类分页查询")
    @GetMapping("/get/list/{categoryId}")
    public Result getList(@PathVariable Long categoryId) {
        List<Task> list=taskService.getList(categoryId);
        return Result.success(list);
    }

    @Operation(summary = "查询当日任务")
    @GetMapping("/get/today")
    public Result getToday() {
        // 1. 获取今天的开始时间 00:00:00
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        // 2. 获取今天的结束时间 23:59:59.999...
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Task> list = taskService.list(new LambdaQueryWrapper<Task>()
                .between(Task::getCreateTime, startOfDay, endOfDay));
        return Result.success(list);
    }

    @Operation(summary = "查询重要的任务")
    @GetMapping("/get/important")
    public Result getImportant() {
        List<Task> list = taskService.list(new LambdaQueryWrapper<Task>().eq(Task::getIsImportant, Boolean.TRUE));
        return Result.success(list);
    }

    @Operation(summary = "查询过期任务")
    @GetMapping("/get/overdue")
    public Result getOverdue() {
        List<Task> list = taskService.list(new LambdaQueryWrapper<Task>().lt(Task::getRemindTime, LocalDateTime.now()));
        return Result.success(list);
    }

















































}
