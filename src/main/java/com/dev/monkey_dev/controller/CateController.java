package com.dev.monkey_dev.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dev.monkey_dev.controller.base.BaseApiRestController;
import com.dev.monkey_dev.dto.request.CategoryRequestDto;
import com.dev.monkey_dev.dto.request.CategorySummaryDto;
import com.dev.monkey_dev.service.category.ICategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category API")
public class CateController extends BaseApiRestController{

    private final ICategoryService categoryService;

    @Operation(summary = "Create category", description = "Create a new category")
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        CategorySummaryDto response = categoryService.createCategory(categoryRequestDto);
        return created(response);
    }

    @Operation(summary = "Get all categories", description = "Get all categories")
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<CategorySummaryDto> response = categoryService.getAllCategories();
        return success(response);
    }

    
}
