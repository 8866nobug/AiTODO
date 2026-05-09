package com.wanger.aitodo.pojo.entity;


import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private Long id;
    private String name;
    private String icon;
    private boolean isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
