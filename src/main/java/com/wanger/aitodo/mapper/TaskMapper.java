package com.wanger.aitodo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wanger.aitodo.pojo.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
