package com.wanger.aitodo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanger.aitodo.pojo.dto.TaskDTO;
import com.wanger.aitodo.pojo.dto.UserInputDTO;
import com.wanger.aitodo.pojo.entity.Task;

import java.util.List;

public interface TaskService extends IService<Task> {

    public boolean saveTask(UserInputDTO userInputDTO);

    public boolean deleteTask(Long taskId,Long categoryId);

    void updateTask(TaskDTO taskDTO);

    List<Task> getList(Long categoryId);
}
