package com.wanger.aitodo.pojo.aiVO;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiDateTimeVO {

        @Description("相对于当前时间向前推算的年数，例如：去年传1，不传默认0")
        private Integer year = 0;

        @Description("相对于当前时间向前推算的月数，例如：上个月传1")
        private Integer month = 0;

        @Description("相对于当前时间向前推算的天数，例如：昨天传1，前天传2")
        private Integer day = 0;

        @Description("相对于当前时间向前推算的小时数，例如：上个小时传1")
        private Integer hour = 0;

        @Description("相对于当前时间向前推算的分钟数，例如：前一分钟传1，默认为0")
        private Integer minute = 0;





}
