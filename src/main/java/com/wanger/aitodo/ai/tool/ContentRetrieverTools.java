package com.wanger.aitodo.ai.tool;

import com.wanger.aitodo.pojo.entity.AgentStatus;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ContentRetrieverTools {

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    @Resource
    private ContentRetriever contentRetriever;

    @Autowired
    private ScoringModel scoringModel;

    public String searchUserKnowledge(String query) {
        // 1. 发送状态通知
        wsTemplate.convertAndSend("/topic/agent/status", AgentStatus.of(AgentStatus.Status.ACTING, "🚀 正在进行深度语义检索...", null));

        // 2. 粗筛 (Retrieval)：利用 ContentRetriever 从 Milvus 捞数据
        List<Content> rawContents = contentRetriever.retrieve(new Query(query));

        if (rawContents.isEmpty()) return "未检索到相关历史信息。";

        // 3. 时间衰减重评分 (Time-Aware Processing)
        List<Content> timeWeightedContents = rawContents.stream()
                .sorted((c1, c2) -> Double.compare(calculateTimeScore(c2), calculateTimeScore(c1)))
                .limit(15) // 预过滤，减轻精排模型的计算量和 API 成本
                .collect(Collectors.toList());

        // 4. 重排序 (Re-rank)：调用 Cross-Encoder 模型精排
        List<TextSegment> segmentsToScore = timeWeightedContents.stream()
                .map(Content::textSegment)
                .collect(Collectors.toList());

        // 调用国产模型 API
        Response<List<Double>> rerankResponse = scoringModel.scoreAll(segmentsToScore, query);
        List<Double> scores = rerankResponse.content();

        // 5. 结果组装：取最终最相关的 5 条，并带上时间元数据
        return IntStream.range(0, Math.min(segmentsToScore.size(), scores.size()))
                .boxed()
                .sorted((i, j) -> Double.compare(scores.get(j), scores.get(i)))
                .limit(5)
                .map(i -> {
                    TextSegment segment = segmentsToScore.get(i);
                    String time = segment.metadata().getString("created_at");
                    // 带上时间戳返回给大模型，有助于它理解时序
                    return String.format("[记录时间: %s]\n内容: %s", time, segment.text());
                })
                .collect(Collectors.joining("\n---\n"));
    }
    private double calculateTimeScore(Content content) {
        try {
            String createdAt = content.textSegment().metadata().getString("created_at");
            if (createdAt == null) return 0.1;
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.OffsetDateTime.parse(createdAt),
                    java.time.OffsetDateTime.now()
            );
            return Math.exp(-0.05 * Math.max(0, days));
        } catch (Exception e) {
            return 0.01;
        }
    }
}
