package com.sashkomusic.dataloader.reader;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DocumentReaderFactory {
    private final Map<ReaderType, DocumentReader> readers;

    public DocumentReaderFactory(List<DocumentReader> availableReaders) {
        this.readers = availableReaders.stream()
                .collect(Collectors.toMap(DocumentReader::getType, Function.identity()));
    }

    public DocumentReader get(ReaderType readerType) {
        return readers.get(readerType);
    }
}
