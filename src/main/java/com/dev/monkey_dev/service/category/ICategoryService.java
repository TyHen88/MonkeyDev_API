package com.dev.monkey_dev.service.category;
import java.util.List;

import com.dev.monkey_dev.dto.request.CategoryRequestDto;
import com.dev.monkey_dev.dto.request.CategorySummaryDto;

public interface ICategoryService {
    // Define service methods 
    CategorySummaryDto createCategory(CategoryRequestDto categoryDto);

    List<CategorySummaryDto> getAllCategories();
}