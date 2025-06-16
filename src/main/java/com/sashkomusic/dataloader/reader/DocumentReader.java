package com.sashkomusic.dataloader.reader;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DocumentReader {
    List<Document> read(Resource resource);

    ReaderType getType();

    static Document withGeneralMetadata(Document document, Map<String, Object> generalMetadata) {
        Map<String, Object> mergedMetadata = new HashMap<>(document.getMetadata());

        generalMetadata.forEach((key, value) -> {
            if (mergedMetadata.containsKey(key)) {
                @SuppressWarnings("unchecked")
                List<String> existingValues = (List<String>) mergedMetadata.get(key);
                @SuppressWarnings("unchecked")
                List<String> newValues = (List<String>) value;

                List<String> mergedValues = Stream.concat(
                                existingValues.stream(),
                                newValues.stream())
                        .distinct()
                        .collect(Collectors.toList());

                mergedMetadata.put(key, mergedValues);
            } else {
                mergedMetadata.put(key, value);
            }
        });
        return new Document(document.getText(), mergedMetadata);
    }
}