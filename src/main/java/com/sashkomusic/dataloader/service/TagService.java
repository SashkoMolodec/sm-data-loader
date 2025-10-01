package com.sashkomusic.dataloader.service;

import com.sashkomusic.dataloader.client.MainApiClient;
import com.sashkomusic.dataloader.client.dto.TagCategoryDto;
import com.sashkomusic.dataloader.client.dto.TagCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {
    private static final Logger log = LoggerFactory.getLogger(TagService.class);

    private final MainApiClient mainApiClient;

    public TagService(MainApiClient mainApiClient) {
        this.mainApiClient = mainApiClient;
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500), include = Exception.class)
    public List<TagCategoryDto> getAllCategories() {
        List<TagCategoryDto> result = mainApiClient.getTagCategories();
        if (result == null) {
            throw new IllegalStateException("Null response when fetching tag categories");
        }
        return result;
    }

    @Recover
    public List<TagCategoryDto> recoverGetAllCategories(Exception e) {
        log.error("All attempts to fetch tag categories failed. Returning empty list.", e);
        return List.of();
    }

    public String getDocumentTagOptionsString() {
        return getAllCategories().stream()
                .filter(Objects::nonNull)
                .map(tc -> tc.name() + ":" + tc.description())
                .collect(Collectors.joining(",\n"));
    }

    public void createTags(List<Document> docs) {
        Set<TagCreateDto> uniqueTags = extractUniqueTags(docs);
        mainApiClient.createTags(uniqueTags);
    }

    private static Set<TagCreateDto> extractUniqueTags(List<Document> docs) {
        Set<TagCreateDto> uniqueTags = new LinkedHashSet<>();
        for (Document doc : docs) {
            Map<String, Object> metadata = doc.getMetadata();
            metadata.forEach((category, value) -> {
                if (value instanceof Collection<?> collection) {
                    for (Object element : collection) {
                        String name = Objects.toString(element, null);
                        if (name != null && !name.isBlank()) {
                            uniqueTags.add(new TagCreateDto(category, name));
                        }
                    }
                }
            });
        }
        return uniqueTags;
    }
}
