package com.dev.monkey_dev.service.product;

import org.springframework.data.domain.Page;

import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.ProductCreateRequestDto;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.ProductUpdateRequestDto;
import com.dev.monkey_dev.enums.FilterProductCateType;

public interface IProductService {
    // Define service methods here
    void createProduct(ProductCreateRequestDto productCreateRequestDto);

    void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto);

    ProductResponseDto getProductById(Long productId);

    Page<ProductResponseDto> getAllProducts(String categorySlug, FilterProductCateType filterProductCateType,
            CriteriaFilter criteriaFilter);

    void deleteProduct(Long productId);

    ProductResponseDto getProductBySlug(String slug);
}
