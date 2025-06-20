package com.sashkomusic.dataloader;

import com.sashkomusic.dataloader.reader.DocumentReader;
import com.sashkomusic.dataloader.reader.DocumentReaderFactory;
import com.sashkomusic.dataloader.reader.ReaderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.sashkomusic.dataloader.reader.ReaderType.AI;
import static com.sashkomusic.dataloader.reader.ReaderType.DEFAULT;

@SpringBootApplication
public class SMusicDataLoaderApplication {

    private final DocumentReaderFactory documentReaderFactory;

    private DocumentReader documentReader;

    private static final Logger LOGGER = LoggerFactory.getLogger(SMusicDataLoaderApplication.class);

    public SMusicDataLoaderApplication(DocumentReaderFactory documentReaderFactory) {
        this.documentReaderFactory = documentReaderFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(SMusicDataLoaderApplication.class, args);
    }

    @Bean
    ApplicationRunner go(FunctionCatalog catalog) {
        Runnable composedFunction = catalog.lookup(null);
        return args -> composedFunction.run();
    }

    @Bean
    Function<Message<byte[]>, Message<byte[]>> readerResolver() {
        return message -> {
            String contentType = message.getHeaders().get("file_name").toString();

            ReaderType readerType = DEFAULT;
            if (contentType.contains("_AI")) {
                readerType = AI;
            }
            setDocumentReader(documentReaderFactory.get(readerType));

            return message;
        };
    }

    @Bean
    Function<Message<byte[]>, List<Document>> documentReader() {
        return message -> {
            ByteArrayResource resource = new ByteArrayResource(message.getPayload());
            return documentReader.read(resource);
        };
    }

    @Bean
    Consumer<List<Document>> vectorStoreConsumer(VectorStore vectorStore) {
        return documents -> {
            long docCount = documents.size();
            LOGGER.info("Writing {} documents to vector store.", docCount);

            List<Document> filtered = documents.stream()
                    .filter(document -> !document.getText().isEmpty()).toList();

            vectorStore.accept(filtered);

            LOGGER.info("{} documents have been written to vector store.", docCount);
        };
    }

    public void setDocumentReader(DocumentReader documentReader) {
        this.documentReader = documentReader;
    }
}
