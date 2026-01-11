package com.dev.monkey_dev.service.category;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.dev.monkey_dev.domain.entity.Category;
import com.dev.monkey_dev.domain.respository.CategoryRepository;
import com.dev.monkey_dev.dto.request.CategoryRequestDto;
import com.dev.monkey_dev.dto.request.CategorySummaryDto;

@Service
@RequiredArgsConstructor
public class CateServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategorySummaryDto createCategory(CategoryRequestDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.name());
        category.setSlug(categoryDto.slug());
        category.setDescription(categoryDto.description());
        category.setImageUrl(categoryDto.imageUrl());
        category.setIsActive(categoryDto.isActive());
        if (categoryDto.parentId() != null) {
            category.setParent(categoryRepository.getReferenceById(categoryDto.parentId()));
        }
        Category saved = categoryRepository.save(category);
        return toSummary(saved);
    }

    @Override
    public List<CategorySummaryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    private CategorySummaryDto toSummary(Category category) {
        return new CategorySummaryDto(category.getId(), category.getName(), category.getSlug());
    }
}