package com.dev.monkey_dev.service.category;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.domain.entity.Category;
import com.dev.monkey_dev.domain.entity.Products;
import com.dev.monkey_dev.domain.respository.CategoryRepository;
import com.dev.monkey_dev.domain.respository.ProductRepository;
import com.dev.monkey_dev.dto.request.CategoryRequestDto;
import com.dev.monkey_dev.dto.request.CategorySummaryDto;
import com.dev.monkey_dev.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class CateServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public List<CategorySummaryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    private CategorySummaryDto toSummary(Category category) {
        return new CategorySummaryDto(category.getId(), category.getName(), category.getSlug());
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found");
            }
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found"));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category is not active");
            }
            category.setIsActive(false);
            categoryRepository.save(category);

        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR,
                    "Failed to delete category: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CategorySummaryDto getCategoryById(Long categoryId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found");
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found"));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category is not active");
            }
            return toSummary(category);
        } catch (Exception e) {
            throw new BusinessException(StatusCode.BAD_REQUEST,
                    "Failed to get category by id: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateCategory(Long categoryId, CategoryRequestDto categoryDto) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found");
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found"));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category is not active");
            }
            category.setName(categoryDto.name());
            category.setSlug(categoryDto.slug());
            category.setDescription(categoryDto.description());
            category.setImageUrl(categoryDto.imageUrl());
            category.setIsActive(categoryDto.isActive());
            if (categoryDto.parentId() != null) {
                category.setParent(categoryRepository.getReferenceById(categoryDto.parentId()));
            }
            categoryRepository.save(category);
        } catch (Exception e) {
            throw new BusinessException(StatusCode.BAD_REQUEST,
                    "Failed to update category: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeProductFromCategory(Long categoryId, Long productId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found");
            }

            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found"));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category is not active");
            }
            productRepository.removeProductFromCategory(categoryId, productId);
        } catch (Exception e) {
            throw new BusinessException(StatusCode.BAD_REQUEST,
                    "Failed to remove product from category: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void addProductToCategory(Long categoryId, Long productId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found");
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found"));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category is not active");
            }
            productRepository.addProductToCategory(categoryId, productId);
        } catch (Exception e) {
            throw new BusinessException(StatusCode.BAD_REQUEST,
                    "Failed to add product to category: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void bulkDeleteProductsFromCategory(Long categoryId, List<Long> productIds) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found");
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found"));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category is not active");
            }
            for (Long productId : productIds) {
                productRepository.removeProductFromCategory(categoryId, productId);
            }
        } catch (Exception e) {
            throw new BusinessException(StatusCode.BAD_REQUEST,
                    "Failed to bulk delete products from category: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void bulkAddProductsToCategory(Long categoryId, List<Long> productIds) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found");
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category not found"));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND, "Category is not active");
            }
            var products = productRepository.findAllById(productIds);
            if (products.isEmpty()) {
                throw new BusinessException(StatusCode.PRODUCT_NOT_FOUND, "No products found");
            }
            for (Products product : products) {
                if (product.getCategories().contains(category)) {
                    throw new BusinessException(StatusCode.PRODUCT_ALREADY_IN_CATEGORY, "Product already in category");
                }
                product.getCategories().add(category);
            }
            productRepository.saveAll(products);

        } catch (Exception e) {
            throw new BusinessException(StatusCode.BAD_REQUEST,
                    "Failed to bulk add products to category: " + e.getMessage());
        }
    }
}