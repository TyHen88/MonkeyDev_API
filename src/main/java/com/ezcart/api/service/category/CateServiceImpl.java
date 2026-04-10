package com.ezcart.api.service.category;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.ezcart.api.common.api.StatusCode;
import com.ezcart.api.domain.entity.Category;
import com.ezcart.api.domain.entity.Products;
import com.ezcart.api.domain.respository.CategoryRepository;
import com.ezcart.api.domain.respository.ProductRepository;
import com.ezcart.api.dto.request.CategoryRequestDto;
import com.ezcart.api.dto.response.CategorySummaryDto;
import com.ezcart.api.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class CateServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CategorySummaryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    private CategorySummaryDto toSummary(Category category) {
        return new CategorySummaryDto(category.getId(), category.getName(), category.getSlug());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.INVALID_CATEGORY_ID);
            }
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_ACTIVE);
            }
            category.setIsActive(false);
            categoryRepository.save(category);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CategorySummaryDto getCategoryById(Long categoryId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.INVALID_CATEGORY_ID);
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_ACTIVE);
            }
            return toSummary(category);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long categoryId, CategoryRequestDto categoryDto) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.INVALID_CATEGORY_ID);
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_ACTIVE);
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
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProductFromCategory(Long categoryId, Long productId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.INVALID_CATEGORY_ID);
            }

            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_ACTIVE);
            }
            productRepository.removeProductFromCategory(categoryId, productId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProductToCategory(Long categoryId, Long productId) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.INVALID_CATEGORY_ID);
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_ACTIVE);
            }
            productRepository.addProductToCategory(categoryId, productId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bulkDeleteProductsFromCategory(Long categoryId, List<Long> productIds) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.INVALID_CATEGORY_ID);
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_ACTIVE);
            }
            for (Long productId : productIds) {
                productRepository.removeProductFromCategory(categoryId, productId);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bulkAddProductsToCategory(Long categoryId, List<Long> productIds) {
        try {
            // Validate category ID
            if (categoryId == null || categoryId <= 0) {
                throw new BusinessException(StatusCode.INVALID_CATEGORY_ID);
            }
            var category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(StatusCode.CATEGORY_NOT_FOUND));
            if (!category.getIsActive()) {
                throw new BusinessException(StatusCode.CATEGORY_NOT_ACTIVE);
            }
            var products = productRepository.findAllById(productIds);
            if (products.isEmpty()) {
                throw new BusinessException(StatusCode.PRODUCTS_NOT_FOUND);
            }
            for (Products product : products) {
                if (product.getCategories().contains(category)) {
                    throw new BusinessException(StatusCode.PRODUCT_ALREADY_IN_CATEGORY);
                }
                product.getCategories().add(category);
            }
            productRepository.saveAll(products);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}

