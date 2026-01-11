package com.dev.monkey_dev.service.product;

import java.util.List;

import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.ProductCreateRequestDto;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.ProductUpdateRequestDto;

public interface IProductService {
    // Define service methods here
    ProductResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto);

    ProductResponseDto updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto);

    ProductResponseDto getProductById(Long productId);

    List<ProductResponseDto> getAllProducts(CriteriaFilter criteriaFilter);  
}
