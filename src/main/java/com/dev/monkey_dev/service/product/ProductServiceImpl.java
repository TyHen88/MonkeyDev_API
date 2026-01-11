package com.dev.monkey_dev.service.product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dev.monkey_dev.domain.entity.Category;
import com.dev.monkey_dev.domain.entity.Products;
import com.dev.monkey_dev.domain.respository.CategoryRepository;
import com.dev.monkey_dev.domain.respository.ProductRepository;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.dto.mapper.ProductMapper;
import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.ProductCreateRequestDto;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.ProductUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
       private final ProductRepository productRepository;
       private final UserRepository userRepository;
       private final CategoryRepository categoryRepository;
       private final ProductMapper productMapper;

       public ProductResponseDto createProduct(ProductCreateRequestDto productCreateRequestDto) {
              Products product = productMapper.toEntity(productCreateRequestDto);
              product.setUser(userRepository.getReferenceById(productCreateRequestDto.userId()));

              Set<Category> categories = new HashSet<>(
                            categoryRepository.findAllById(productCreateRequestDto.categoryIds()));
              product.setCategories(categories);

              if (productCreateRequestDto.images() != null && !productCreateRequestDto.images().isEmpty()) {
                     product.setImageUrl(productCreateRequestDto.images().get(0).imageUrl());
              }

              Products saved = productRepository.save(product);
              return productMapper.toResponse(saved);
       }

       public ProductResponseDto updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {
              Products product = productRepository.findById(productId).orElseThrow();
              productMapper.updateEntity(productUpdateRequestDto, product);

              Set<Category> categories = new HashSet<>(
                            categoryRepository.findAllById(productUpdateRequestDto.categoryIds()));
              product.setCategories(categories);

              Products saved = productRepository.save(product);
              return productMapper.toResponse(saved);
       }

       public ProductResponseDto getProductById(Long productId) {
              Products product = productRepository.findById(productId).orElseThrow();
              return productMapper.toResponse(product);
       }

       public List<ProductResponseDto> getAllProducts(CriteriaFilter criteriaFilter) {
              return productRepository.findAll(criteriaFilter.toPageable()).stream()
                            .map(productMapper::toResponse)
                            .toList();
       }
}
