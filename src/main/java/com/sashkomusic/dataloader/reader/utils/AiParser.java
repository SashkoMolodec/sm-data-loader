package com.sashkomusic.dataloader.reader.utils;

import com.sashkomusic.dataloader.SMusicDataLoaderApplication;
import com.sashkomusic.dataloader.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiParser {

    private static final Logger LOG = LoggerFactory.getLogger(SMusicDataLoaderApplication.class);

    @Value("classpath:/promptTemplates/system/default.st")
    Resource systemPrompt;

    @Value("classpath:/promptTemplates/generalMetadataAsk.st")
    Resource generalMetadataPrompt;

    @Value("classpath:/promptTemplates/parseTextChunk.st")
    Resource parseTextChunkPrompt;

    @Value("classpath:/promptTemplates/parsePdfChunk.st")
    Resource parsePdfPrompt;

    private final ChatClient chatClient;
    private final TagService tagService;

    public AiParser(AnthropicChatModel chatModel, TagService tagService) {
        this.chatClient = ChatClient.create(chatModel);
        this.tagService = tagService;
    }

    public Map<String, Object> askGeneralMetadata(Resource resource) {
        Prompt prompt = buildGeneralMetadataAskPrompt("");
        UserMessage message = UserMessage.builder()
                .media(new Media(new MimeType("application", "pdf"), resource))
                .text(prompt.getContents())
                .build();

        var metadata = chatClient
                .prompt(new Prompt(message))
                .system(systemPrompt)
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {
                });

        LOG.info("Extracted metadata: {}", metadata);
        return metadata;
    }

    public Map<String, Object> askGeneralMetadata(String text) {
        Prompt prompt = buildGeneralMetadataAskPrompt(text);

        var metadata = chatClient
                .prompt(prompt)
                .system(systemPrompt)
                .call()
                .entity(new ParameterizedTypeReference<Map<String, Object>>() {
                });

        LOG.info("Extracted metadata: {}", metadata);
        return metadata;
    }

    private Prompt buildGeneralMetadataAskPrompt(String text) {
        return PromptTemplate.builder()
                .resource(generalMetadataPrompt)
                .variables(Map.of("text", text))
                .build().create();
    }

    public Document analyzePdf(Resource resource) {
        var prompt = PromptTemplate.builder()
                .resource(parsePdfPrompt)
                .variables(Map.of("tagCategories", tagService.getDocumentTagOptionsString()))
                .build().create();

        var withMedia = UserMessage.builder()
                .media(new Media(new MimeType("application", "pdf"), resource))
                .text(prompt.getContents())
                .build();

        var response = chatClient
                .prompt(new Prompt(withMedia))
                .system(systemPrompt)
                .call()
                .entity(ResponseDocument.class);

        LOG.info("Extracted text from resource using AI parser");
        return buildDocs(response);
    }

    public Document analyzeText(String text) {
        var prompt = PromptTemplate.builder()
                .resource(parseTextChunkPrompt)
                .variables(Map.of("text", text, "tagCategories", tagService.getDocumentTagOptionsString()))
                .build().create();

        var response = chatClient
                .prompt(prompt)
                .system(systemPrompt)
                .call()
                .entity(ResponseDocument.class);

        return buildDocs(response);
    }

    private Document buildDocs(ResponseDocument response) {
        return new Document(response.text(), new HashMap<>(response.metadata()));
    }
}

record ResponseDocument(String text, Map<String, List<String>> metadata) {
}
