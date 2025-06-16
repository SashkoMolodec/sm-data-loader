package com.sashkomusic.dataloader.reader;

import com.sashkomusic.dataloader.reader.utils.AiParser;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DefaultDocumentReader implements DocumentReader {

    public static final int CHUNK_SIZE = 1000;

    public final AiParser aiParser;

    public DefaultDocumentReader(AiParser aiParser) {
        this.aiParser = aiParser;
    }

    @Override
    public List<Document> read(Resource resource) {
        List<Document> docsToAnalyze = TokenTextSplitter.builder()
                .withChunkSize(CHUNK_SIZE)
                .build()
                .apply(new TikaDocumentReader(resource).get());

        Map<String, Object> generalMetadata = aiParser.askGeneralMetadata(docsToAnalyze.getFirst().getText());

        var analyzed = new ArrayList<Document>();
        for (Document doc : docsToAnalyze) {
            Document processed = aiParser.analyzeText(doc.getText());
            analyzed.add(DocumentReader.withGeneralMetadata(processed, generalMetadata));
        }
        return analyzed;
    }

    @Override
    public ReaderType getType() {
        return ReaderType.DEFAULT;
    }
}
