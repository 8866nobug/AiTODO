package com.wanger.aitodo.ai.tool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

@Component
public class PdfExportTools {

    private final String uploadPath = "D:/ai-todo/exports/";
    private final String urlPrefix = "http://localhost:8080/files/";
    // 你需要下载一个中文字体文件放到 resources 下，比如 simsun.ttf
    private final String fontPath = "src/main/resources/fonts/simsunb.ttf";

    public String generateInfoPdf(
             String title,
            String optimizedContent
    ) {
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        String fileName = title.replaceAll("[\\\\/:*?\"<>|]", "_") + "_" + System.currentTimeMillis() + ".pdf";
        String fullPath = uploadPath + fileName;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // 加载中文字体 (PDFBox 必须加载外部字体才能显示中文)
            PDType0Font font = PDType0Font.load(document, new File(fontPath));

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 18);
                contentStream.newLineAtOffset(50, 750); // 设置起始坐标
                contentStream.showText(title);

                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("生成时间: " + LocalDateTime.now());

                contentStream.newLineAtOffset(0, -30);
                // 简单的换行逻辑：PDFBox 不会自动换行，这里按行切分
                String[] lines = optimizedContent.split("\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -20); // 每一行向下偏移
                }
                contentStream.endText();
            }

            document.save(fullPath);
            return "PDF 已成功生成，下载地址为: " + urlPrefix + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "生成 PDF 失败: " + e.getMessage();
        }
    }
}