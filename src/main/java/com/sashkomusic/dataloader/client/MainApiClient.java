package com.sashkomusic.dataloader.client;

import com.sashkomusic.dataloader.client.dto.TagCategoryDto;
import com.sashkomusic.dataloader.client.dto.TagCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

@Component
public class MainApiClient {
    private static final Logger LOG = LoggerFactory.getLogger(MainApiClient.class);

    private final RestClient restClient;

    @Value("${main.api.tags.create-path:/tags}")
    private String tagsCreatePath;

    @Value("${main.api.tags.categories-path:/tags/categories}")
    private String tagsCategoriesPath;

    public MainApiClient(@Value("${main.api.base-url:http://localhost:8080}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void createTags(Set<TagCreateDto> tagsToCreate) {
        try {
            restClient.post()
                    .uri(tagsCreatePath)
                    .body(tagsToCreate).retrieve()
                    .toBodilessEntity();
            LOG.info("Created/queued {} tags to main API", tagsToCreate.size());
        } catch (Exception e) {
            LOG.warn("Failed to create tags in main API: {}", e.getMessage());
        }
    }

    public List<TagCategoryDto> getTagCategories() {
        return restClient.get()
                .uri(tagsCategoriesPath)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<TagCategoryDto>>() {
                });
    }
}
