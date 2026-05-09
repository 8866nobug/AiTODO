package com.wanger.aitodo.pojo.aiVO;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiTaskFlowVO {
    @Description("简单任务只添加一个AiTaskVO,复杂任务创建多个AiTaskVO,按照执行顺序排列")
    private List<AiTaskVO> taskFlow;
}
