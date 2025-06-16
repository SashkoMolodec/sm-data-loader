package com.sashkomusic.dataloader.reader.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PDFUtils {

    public static List<Resource> splitPdf(Resource pdfResource, int pageNumberDelimiter) {
        try {
            Path tempDir = Files.createTempDirectory("pdf_chunks");
            String pdfResourceName = getPdfResourceName(pdfResource);
            List<Resource> result = new ArrayList<>();

            byte[] pdfBytes = pdfResource.getContentAsByteArray();

            try (PDDocument sourceDocument = Loader.loadPDF(pdfBytes)) {
                int totalPages = sourceDocument.getNumberOfPages();
                int firstPartPages = Math.min(pageNumberDelimiter, totalPages);

                // Create first part (first n pages)
                try (PDDocument firstPartDocument = new PDDocument()) {
                    for (int i = 0; i < firstPartPages; i++) {
                        PDPage page = sourceDocument.getPage(i);
                        firstPartDocument.addPage(page);
                    }

                    String firstFileName = String.format(
                            "first_%d_pages_of_%s.pdf",
                            firstPartPages,
                            pdfResourceName
                    );

                    Path firstPath = tempDir.resolve(firstFileName);
                    firstPartDocument.save(firstPath.toFile());
                    result.add(new FileSystemResource(firstPath.toFile()));
                }

                // Create second part (remaining pages)
                if (firstPartPages < totalPages) {
                    try (PDDocument secondPartDocument = new PDDocument()) {
                        for (int i = firstPartPages; i < totalPages; i++) {
                            PDPage page = sourceDocument.getPage(i);
                            secondPartDocument.addPage(page);
                        }

                        String secondFileName = String.format(
                                "remaining_pages_%d_to_%d_of_%s.pdf",
                                firstPartPages + 1,
                                totalPages,
                                pdfResourceName
                        );

                        Path secondPath = tempDir.resolve(secondFileName);
                        secondPartDocument.save(secondPath.toFile());
                        result.add(new FileSystemResource(secondPath.toFile()));
                    }
                }

                return result;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Resource getFirstNPagesPdf(Resource pdfResource, int pagesCount) {
        try {
            Path tempDir = Files.createTempDirectory("pdf_chunks");

            try (PDDocument sourceDocument = Loader.loadPDF(pdfResource.getContentAsByteArray());
                 PDDocument targetDocument = new PDDocument()) {

                int pagesToExtract = Math.min(pagesCount, sourceDocument.getNumberOfPages());

                for (int i = 0; i < pagesToExtract; i++) {
                    PDPage page = sourceDocument.getPage(i);
                    targetDocument.addPage(page);
                }

                String pdfResourceName = getPdfResourceName(pdfResource);
                String fileName = String.format(
                        "first_%d_pages_of_%s.pdf",
                        pagesCount,
                        pdfResourceName
                );

                Path path = tempDir.resolve(fileName);
                targetDocument.save(path.toFile());

                return new FileSystemResource(path.toFile());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String getPdfResourceName(Resource pdfResource) {
        return pdfResource.getFilename() != null ?
                pdfResource.getFilename().replace(".pdf", "") : String.valueOf(System.currentTimeMillis());
    }

    public static List<Resource> splitPdfIntoChunks(Resource pdfResource, int pagesPerChunk) {
        List<Resource> chunkResources = new ArrayList<>();

        try {
            // Створюємо тимчасову директорію для зберігання частин
            Path tempDir = Files.createTempDirectory("pdf_chunks");

            try (PDDocument document = Loader.loadPDF(pdfResource.getContentAsByteArray())) {
                int totalPages = document.getNumberOfPages();
                int totalChunks = (int) Math.ceil((double) totalPages / pagesPerChunk);

                for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
                    // Створюємо новий документ для частини
                    try (PDDocument chunkDocument = new PDDocument()) {
                        // Визначаємо діапазон сторінок для поточної частини
                        int startPage = chunkIndex * pagesPerChunk;
                        int endPage = Math.min(startPage + pagesPerChunk, totalPages);

                        // Додаємо сторінки до нового документа
                        for (int pageIndex = startPage; pageIndex < endPage; pageIndex++) {
                            PDPage page = document.getPage(pageIndex);
                            chunkDocument.addPage(page);
                        }

                        // Зберігаємо частину як окремий PDF
                        String chunkFileName = String.format(
                                "%s_chunk_%d_of_%d.pdf",
                                getPdfResourceName(pdfResource),
                                chunkIndex + 1,
                                totalChunks
                        );

                        // Зберігаємо в тимчасову директорію
                        Path chunkPath = tempDir.resolve(chunkFileName);
                        chunkDocument.save(chunkPath.toFile());

                        // Створюємо ресурс з файлу
                        Resource chunkResource = new FileSystemResource(chunkPath.toFile());
                        chunkResources.add(chunkResource);
                    }
                }
            }
            return chunkResources;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
