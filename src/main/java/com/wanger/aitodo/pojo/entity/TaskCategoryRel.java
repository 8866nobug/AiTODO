package com.wanger.aitodo.pojo.entity;

import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCategoryRel {
    private Long taskId;
    private Long categoryId;
    private LocalDateTime createTime;
}
