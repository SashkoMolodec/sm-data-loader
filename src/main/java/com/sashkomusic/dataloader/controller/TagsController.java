package com.sashkomusic.dataloader.controller;

import com.sashkomusic.dataloader.client.dto.TagCategoryDto;
import com.sashkomusic.dataloader.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    private final TagService tagService;

    public TagsController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<TagCategoryDto>> getCategories() {
        List<TagCategoryDto> categories = tagService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.status(503).body(categories);
        }
        return ResponseEntity.ok(categories);
    }
}
