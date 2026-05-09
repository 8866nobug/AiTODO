package com.wanger.aitodo.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class PdfCleanTask {

    private final String uploadPath = "D:/ai-todo/exports/";

    /**
     * 秒 分 时 日 月 周
     * 0 0 8 * * ? 表示每天早上 8:00:00 执行
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void cleanOldPdfs() {
        log.info("开始执行 PDF 清理任务...");
        File directory = new File(uploadPath);

        if (!directory.exists() || !directory.isDirectory()) {
            log.warn("目录 {} 不存在，跳过清理", uploadPath);
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files == null || files.length == 0) {
            log.info("文件夹中无 PDF 文件，无需清理");
            return;
        }

        // 计算一周前的时间点
        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        int deleteCount = 0;

        for (File file : files) {
            try {
                // 获取文件创建时间（比读取文件名更可靠）
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                Instant creationTime = attrs.creationTime().toInstant();

                if (creationTime.isBefore(oneWeekAgo)) {
                    if (file.delete()) {
                        log.info("已删除过期 PDF 文件: {}", file.getName());
                        deleteCount++;
                    }
                }
            } catch (Exception e) {
                log.error("处理文件 {} 时发生错误: {}", file.getName(), e.getMessage());
            }
        }
        log.info("清理任务完成，共删除 {} 个过期文件。", deleteCount);
    }
}