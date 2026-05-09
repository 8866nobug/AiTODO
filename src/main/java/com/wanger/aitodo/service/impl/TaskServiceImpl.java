package com.wanger.aitodo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanger.aitodo.ai.service.AiBaseService;
import com.wanger.aitodo.mapper.TaskCategoryRelMapper;
import com.wanger.aitodo.mapper.TaskMapper;
import com.wanger.aitodo.pojo.aiVO.AiDateTimeVO;
import com.wanger.aitodo.pojo.aiVO.AiTaskFlowVO;
import com.wanger.aitodo.pojo.aiVO.AiTaskVO;
import com.wanger.aitodo.pojo.dto.TaskDTO;
import com.wanger.aitodo.pojo.dto.UserInputDTO;
import com.wanger.aitodo.pojo.entity.TaskCategoryRel;
import com.wanger.aitodo.pojo.entity.Task;
import com.wanger.aitodo.service.CategoryService;
import com.wanger.aitodo.service.TaskService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Resource
    private CategoryService categoryService;

    @Resource
    private TaskCategoryRelMapper taskCategoryRelMapper;

    @Resource
    private AiBaseService aiBaseService;

    @Resource
    private EmbeddingStoreIngestor  embeddingStoreIngestor;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean saveTask(UserInputDTO userInputDTO) {
        //将用户输入交给Ai分解优化
        AiTaskFlowVO aiTaskFlowVO = aiBaseService.analyseTaskFlow(userInputDTO.getTitle());
        //将Ai优化后的工作流拼接并向量化
        List<AiTaskVO> taskFlow = aiTaskFlowVO.getTaskFlow();
        String content = taskFlow.stream()
                .map(AiTaskVO::getContent)
                .collect(Collectors.joining());
        Metadata  metadata = new Metadata();
        metadata.put("title", userInputDTO.getTitle());
        metadata.put("content", content);
        metadata.put("categoryId", userInputDTO.getCategoryId());
        metadata.put("timestamp", LocalDateTime.now(ZoneId.systemDefault()).toString());
        Document document = Document.from(userInputDTO.getTitle() + "\n\n" + content,metadata);
        embeddingStoreIngestor.ingest(document);
        //将Ai生成的Task保存到数据库中
        taskFlow.forEach(taskVO->{
             Task task = new Task();
             BeanUtils.copyProperties(taskVO,task);
             task.setCreateTime(LocalDateTime.now());
            AiDateTimeVO dateTimeVO = taskVO.getAiDateTimeVO();
            task.setRemindTime(LocalDateTime.now()
                     .plusYears(dateTimeVO.getYear())
                     .plusMonths(dateTimeVO.getMonth())
                    .plusDays(dateTimeVO.getDay())
                    .plusHours(dateTimeVO.getHour())
                    .plusMinutes(dateTimeVO.getMinute()));
             save(task);
             TaskCategoryRel taskCategoryRel = new TaskCategoryRel();
             taskCategoryRel.setTaskId(task.getId());
             taskCategoryRel.setCategoryId(userInputDTO.getCategoryId());
             taskCategoryRel.setCreateTime(LocalDateTime.now());
             taskCategoryRelMapper.insert(taskCategoryRel);
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean deleteTask(Long taskId, Long categoryId) {
        removeById(taskId);
        taskCategoryRelMapper.delete(new LambdaQueryWrapper<TaskCategoryRel>()
                .eq(TaskCategoryRel::getTaskId,taskId)
                .eq(TaskCategoryRel::getCategoryId, categoryId));

        return true;
    }

    @Override
    public void updateTask(TaskDTO taskDTO) {
        update(new  LambdaUpdateWrapper<Task>()
                .eq(Task::getId,taskDTO.getId())
                .set(Task::getTitle,taskDTO.getTitle())
                .set(Task::getRemindTime,taskDTO.getRemindTime())
                .set(Task::getStatus,taskDTO.getStatus())
                .set(Task::getIsImportant,taskDTO.getIsImportant()));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public List<Task> getList(Long categoryId) {
        List<Task> taskList = new ArrayList<>();
            List<TaskCategoryRel> taskCategoryRels = taskCategoryRelMapper.selectList(new LambdaQueryWrapper<TaskCategoryRel>().eq(TaskCategoryRel::getCategoryId, categoryId));
            taskCategoryRels.forEach(taskCategoryRel -> {
                Task task = this.getById(taskCategoryRel.getTaskId());
                taskList.add(task);
            });

        return taskList;
    }
}
