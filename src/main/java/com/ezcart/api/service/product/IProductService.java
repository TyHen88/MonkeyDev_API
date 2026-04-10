package com.ezcart.api.service.product;

import org.springframework.data.domain.Page;

import com.ezcart.api.dto.request.CriteriaFilter;
import com.ezcart.api.dto.request.ProductCreateRequestDto;
import com.ezcart.api.dto.request.ProductUpdateRequestDto;
import com.ezcart.api.dto.response.ProductResponseDto;
import com.ezcart.api.enums.FilterProductCateType;

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

