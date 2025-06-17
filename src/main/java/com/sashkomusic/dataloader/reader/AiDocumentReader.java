package com.sashkomusic.dataloader.reader;

import com.sashkomusic.dataloader.reader.utils.AiParser;
import com.sashkomusic.dataloader.reader.utils.PDFUtils;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sashkomusic.dataloader.reader.utils.PDFUtils.splitPdfIntoChunks;

@Component
public class AiDocumentReader implements DocumentReader {
    private static final int DOCUMENT_HEADER_PAGE_NUMBER_DELIMITER = 2;
    private static final int PAGES_PER_CHUNK = 1;

    private final AiParser aiParser;

    public AiDocumentReader(AiParser aiParser) {
        this.aiParser = aiParser;
    }

    @Override
    public List<Document> read(Resource resource) {
        List<Resource> splitPdf = PDFUtils.splitPdf(resource, DOCUMENT_HEADER_PAGE_NUMBER_DELIMITER);
        Map<String, Object> generalMetadata = aiParser.askGeneralMetadata(splitPdf.getFirst());

        List<Document> docs = new ArrayList<>();
        List<Resource> chunkResources = splitPdfIntoChunks(splitPdf.getLast(), PAGES_PER_CHUNK);
        for (Resource chunkResource : chunkResources) {
            Document analyzed = aiParser.analyzePdf(chunkResource);
            docs.add(DocumentReader.withGeneralMetadata(analyzed, generalMetadata));
        }
        return docs;
    }

    @Override
    public ReaderType getType() {
        return ReaderType.AI;
    }
}
