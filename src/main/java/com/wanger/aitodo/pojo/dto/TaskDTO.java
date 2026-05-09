package com.wanger.aitodo.pojo.dto;

import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long categoryId;

    private Long id;
    private String title;
    private Boolean status=false;
    private Boolean isImportant=false;
    private LocalDateTime remindTime;
    private LocalDateTime completedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
