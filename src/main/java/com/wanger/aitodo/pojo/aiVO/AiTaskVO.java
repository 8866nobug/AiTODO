package com.wanger.aitodo.pojo.aiVO;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;


@Data
public class AiTaskVO {
    @Description("优化后的任务标题")
    private String  title;

    @Description("优化后的任务内容，有具体的目标时间，可量化数据")
    private String content;

    @Description("是否是关键任务:只对复杂任务分解后的子任务生效，简单任务一律为false")
    private Boolean isImportant;

    @Description("任务持续时间。根据时间颗粒度选择填写，常用单位：day(天)、hour(小时)。长周期用month/year，短周期用minute")
    private AiDateTimeVO aiDateTimeVO;

}
