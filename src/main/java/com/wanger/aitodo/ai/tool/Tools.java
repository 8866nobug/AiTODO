package com.wanger.aitodo.ai.tool;

import com.wanger.aitodo.pojo.aiVO.AiDateTimeVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Tools {

    @Resource
    private ContentRetrieverTools contentRetrieverTools;

    @Resource
    private QueryTaskTools queryTaskTools;

    @Resource
    private PdfExportTools pdfExportTools;

    @Resource
    private TimeTools timeTools;

    @Tool("获取当前的精确系统时间")
    public String getCurrentTime() {
        return  timeTools.getCurrentTime();
    }

    @Tool("""
        从用户的历史任务、过去一周的计划和个人习惯中检索相关信息。
        ⚠️注意：在调用此工具前，必须先调用getCurrentTime工具获取精确的系统时间
        当用户提到'以前'、'习惯'、'参考进度'或需要制定新计划时使用此工具。
        """)
    public String searchHistory(String query) {
        return contentRetrieverTools.searchUserKnowledge(query);
    }

    @Tool("""
            用于‘回顾过去’。查询用户在过去一段时间内创建或完成的任务记录（基于任务创建时间）。
            ⚠️注意：在调用此工具前，必须先调用getCurrentTime工具获取精确的系统时间
            适用于：总结昨天做了什么、查看上个月的完成情况、回顾历史足迹。
          """
    )
    public String queryHistoryTask(@P("任务状态：true已完成，false未完成") Boolean status,
                            @P("是否重要：true重要，false不重要") Boolean isImportant,
                            @P("距离当前时间向前回溯的时间量。例如查‘昨天’则day填1，查‘上个月’则month填1") AiDateTimeVO aiDateTimeVO) {
        return queryTaskTools.queryHistoryTasks(status,isImportant,aiDateTimeVO);
    }

    @Tool("""
            用于‘规划未来’。查询用户在未来一段时间内需要处理的待办日程（基于提醒或截止时间）。
            适用于：查看明天有什么安排、了解下周的截止日期(DDL)、检查未来的计划。""")
    public String queryFutureTask(@P("任务状态：通常传false(查询未完成)，true表示已提前完成的未来日程") Boolean status,
                                  @P("是否重要：true重要任务，false普通任务") Boolean isImportant,
                                  @P("距离当前时间向后推算的计划范围。例如查‘明天’则day填1，查‘下周’则day填7") AiDateTimeVO aiDateTimeVO){
        return queryTaskTools.queryFutureTasks(status,isImportant,aiDateTimeVO);
    }

    @Tool("""
            最后的导出步骤’：将之前通过查询工具获取到的、并经过 AI 优化后的任务总结或周报月报内容，导出为正式的 PDF 文件并返回下载链接。
            ⚠️注意：在调用此工具前，必须先调用 queryHistoryTasks、queryFutureTasks 或 searchUserKnowledge 获取必要素材
            严禁在没有任何数据支撑的情况下直接生成 PDF。""")
    public String pdfCreate(
            @P("PDF 的主标题。应简洁明了，如‘汪二的本周任务总结’") String title,
            @P("由 AI 根据之前查询到的原始数据，进行排版、润色、分类后的正文内容。支持使用 \\\\n 进行换行。") String optimizedContent){
       return pdfExportTools.generateInfoPdf(title,optimizedContent);
    }



}
