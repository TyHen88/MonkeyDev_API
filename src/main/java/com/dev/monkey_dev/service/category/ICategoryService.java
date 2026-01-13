package com.dev.monkey_dev.service.category;

import java.util.List;

import com.dev.monkey_dev.dto.request.CategoryRequestDto;
import com.dev.monkey_dev.dto.request.CategorySummaryDto;

public interface ICategoryService {
    // Define service methods
    CategorySummaryDto createCategory(CategoryRequestDto categoryDto);

    List<CategorySummaryDto> getAllCategories();

    void deleteCategory(Long categoryId);

    CategorySummaryDto getCategoryById(Long categoryId);

    void updateCategory(Long categoryId, CategoryRequestDto categoryDto);

    void removeProductFromCategory(Long categoryId, Long productId);

    void addProductToCategory(Long categoryId, Long productId);

    void bulkDeleteProductsFromCategory(Long categoryId, List<Long> productIds);

    void bulkAddProductsToCategory(Long categoryId, List<Long> productIds);

}