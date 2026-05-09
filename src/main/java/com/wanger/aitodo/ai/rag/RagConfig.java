package com.wanger.aitodo.ai.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.cohere.CohereScoringModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RagConfig {

    @Value("${langchain4j.milvus.collection-name}")
    private String collectionName;


    // 1. 注册向量库 Bean
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(
            @Value("${langchain4j.milvus.host}") String host,
            @Value("${langchain4j.milvus.port}") int port,
            @Value("${langchain4j.milvus.dimension}") int dimension) {
        return MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName(collectionName)
                .dimension(dimension)
                .build();
    }

    @Bean
    public ScoringModel rerankModel(
            @Value("${langchain4j.siliconflow.rerank.base-url}") String baseUrl,
            @Value("${langchain4j.siliconflow.rerank.api-key}") String apiKey,
            @Value("${langchain4j.siliconflow.rerank.model-name}") String modelName
    ) {
        return  CohereScoringModel.builder()
                .baseUrl(baseUrl) // 硅基流动的地址
                .apiKey(apiKey)
                .modelName(modelName) // 智源的顶级国产重排模型
                .build();
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingStore<TextSegment> embeddingStore,EmbeddingModel qwenEmbeddingModel) {

        //切割文档
        DocumentByParagraphSplitter documentByParagraphSplitter = new DocumentByParagraphSplitter(1000,200);
        return  EmbeddingStoreIngestor.builder()
                .documentSplitter(documentByParagraphSplitter)
                .textSegmentTransformer(textSegment -> TextSegment.from(textSegment.text(),textSegment.metadata()))
                .embeddingModel(qwenEmbeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore,EmbeddingModel qwenEmbeddingModel) {

        //定义检索器
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(50)
                .minScore(0.75)
                .build();
    }
}
