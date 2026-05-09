package com.wanger.aitodo.pojo.aiVO;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ai_config")
public class AiConfig {


        @TableId(type = IdType.AUTO)
        private Long id;

        private Integer providerType; // 对应 provider_type

        private String modelName;

        private String apiKey;

        private String baseUrl;

        // 注意：如果是 MySQL 的自动更新，这里可以只读
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updateTime;

}
