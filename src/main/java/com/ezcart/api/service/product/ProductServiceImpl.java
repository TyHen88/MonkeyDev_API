package com.ezcart.api.service.product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.ezcart.api.common.api.StatusCode;
import com.ezcart.api.domain.entity.Category;
import com.ezcart.api.domain.entity.ProductImage;
import com.ezcart.api.domain.entity.ProductVariation;
import com.ezcart.api.domain.entity.Products;
import com.ezcart.api.domain.respository.CategoryRepository;
import com.ezcart.api.domain.respository.ProductRepository;
import com.ezcart.api.domain.respository.ProductVariationRepository;
import com.ezcart.api.domain.respository.ProductImageRepository;
import com.ezcart.api.domain.respository.UserRepository;
import com.ezcart.api.dto.mapper.ProductMapper;
import com.ezcart.api.dto.request.CriteriaFilter;
import com.ezcart.api.dto.request.ProductCreateRequestDto;
import com.ezcart.api.dto.request.ProductImageCreateDto;
import com.ezcart.api.dto.request.ProductUpdateRequestDto;
import com.ezcart.api.dto.request.ProductVariationCreateDto;
import com.ezcart.api.dto.response.ProductResponseDto;
import com.ezcart.api.enums.FilterProductCateType;
import com.ezcart.api.exception.BusinessException;
import com.ezcart.api.exception.ResourceNotFoundException;
import com.ezcart.api.logging.AppLogManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
       private final ProductRepository productRepository;
       private final UserRepository userRepository;
       private final CategoryRepository categoryRepository;
       private final ProductImageRepository productImageRepository;
       private final ProductVariationRepository productVariationRepository;
       private final ProductMapper productMapper;

       private static final String PRODUCT_NOT_FOUND = "Product not found with id: %d";
       private static final String USER_NOT_FOUND = "User not found with id: %d";
       private static final String CATEGORY_NOT_FOUND = "One or more categories not found";
       private static final String CATEGORIES_REQUIRED = "At least one category is required";

       @Transactional(rollbackFor = Exception.class)
       public void createProduct(ProductCreateRequestDto productCreateRequestDto) {
              try {
                     // Validate request
                     if (productCreateRequestDto == null) {
                            AppLogManager.warn(ProductServiceImpl.class, "Product creation request is null");
                            throw new BusinessException(StatusCode.VALIDATION_FAILED);
                     }

                     // Validate user exists
                     if (!userRepository.existsById(productCreateRequestDto.userId())) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format(USER_NOT_FOUND, productCreateRequestDto.userId()));
                            throw new BusinessException(StatusCode.USER_NOT_FOUND);
                     }

                     // // // Validate categories
                     // if (productCreateRequestDto.categoryIds() == null ||
                     // productCreateRequestDto.categoryIds().isEmpty()) {
                     // AppLogManager.warn(ProductServiceImpl.class, CATEGORIES_REQUIRED);
                     // throw new BusinessException(StatusCode.BAD_REQUEST, CATEGORIES_REQUIRED);
                     // }

                     Products product = productMapper.toEntity(productCreateRequestDto);
                     product.setUser(userRepository.getReferenceById(productCreateRequestDto.userId()));

                     Set<Category> categories = new HashSet<>(
                                   categoryRepository.findAllById(productCreateRequestDto.categoryIds()));

                     // // Validate all categories were found
                     // if (categories.size() != productCreateRequestDto.categoryIds().size()) {
                     // AppLogManager.warn(ProductServiceImpl.class, CATEGORY_NOT_FOUND);
                     // throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND);
                     // }

                     product.setCategories(categories);

                     if (productCreateRequestDto.images() != null && !productCreateRequestDto.images().isEmpty()) {
                            product.setImageUrl(productCreateRequestDto.images().get(0).imageUrl());
                     }

                     // Save the product first before saving related entities
                     Products saved = productRepository.save(product);

                     // Now save ProductImages after the product is persisted
                     List<ProductImageCreateDto> productImageCreateDtos = productCreateRequestDto.images();
                     if (productImageCreateDtos != null) {
                            productImageCreateDtos.forEach(image -> {
                                   ProductImage productImage = new ProductImage();
                                   productImage.setImageUrl(image.imageUrl());
                                   productImage.setAltText(image.altText() == null ? "" : image.altText());
                                   productImage.setDisplayOrder(
                                                 image.displayOrder() == null ? 0 : image.displayOrder());
                                   productImage.setIsPrimary(image.isPrimary() == null ? false : image.isPrimary());
                                   productImage.setProduct(saved);
                                   productImageRepository.save(productImage);
                            });
                     }

                     // Now save ProductVariations after the product is persisted
                     List<ProductVariationCreateDto> productVariationCreateDtos = productCreateRequestDto.variations();
                     if (productVariationCreateDtos != null) {
                            productVariationCreateDtos.forEach(variation -> {
                                   ProductVariation productVariation = new ProductVariation();
                                   productVariation.setName(variation.name());
                                   productVariation.setValue(variation.value() == null ? "" : variation.value());
                                   productVariation.setPriceAdjustment(variation.priceAdjustment() == null
                                                 ? BigDecimal.ZERO
                                                 : variation.priceAdjustment());
                                   productVariation.setSku(variation.sku() == null ? "" : variation.sku());
                                   productVariation.setStockQuantity(
                                                 variation.stockQuantity() == null ? 0
                                                               : variation.stockQuantity().intValue());
                                   productVariation.setProduct(saved);
                                   productVariationRepository.save(productVariation);
                            });
                     }
                     AppLogManager.info(ProductServiceImpl.class,
                                   String.format("Product created successfully with id: %d", saved.getId()));
                     // return productMapper.toResponse(saved);
              } catch (BusinessException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   "Error creating product", e);
                     throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
              }
       }

       @Transactional(rollbackFor = Exception.class)
       public void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {
              try {
                     // Validate product ID
                     if (productId == null || productId <= 0) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format("Invalid product ID: %s", productId));
                            throw new BusinessException(StatusCode.INVALID_PRODUCT_ID);
                     }

                     // Validate request
                     if (productUpdateRequestDto == null) {
                            AppLogManager.warn(ProductServiceImpl.class, "Product update request is null");
                            throw new BusinessException(StatusCode.VALIDATION_FAILED);
                     }

                     Products product = productRepository.findById(productId)
                                   .orElseThrow(() -> {
                                          AppLogManager.warn(ProductServiceImpl.class,
                                                        String.format(PRODUCT_NOT_FOUND, productId));
                                          return new ResourceNotFoundException(
                                                        String.format(PRODUCT_NOT_FOUND, productId));
                                   });

                     // Validate categories if provided
                     if (productUpdateRequestDto.categoryIds() != null &&
                                   !productUpdateRequestDto.categoryIds().isEmpty()) {
                            Set<Category> categories = new HashSet<>(
                                          categoryRepository.findAllById(productUpdateRequestDto.categoryIds()));

                            // Validate all categories were found
                            if (categories.size() != productUpdateRequestDto.categoryIds().size()) {
                                   AppLogManager.warn(ProductServiceImpl.class, CATEGORY_NOT_FOUND);
                                   throw new BusinessException(StatusCode.CATEGORY_NOT_FOUND);
                            }

                            product.setCategories(categories);
                     }

                     productMapper.updateEntity(productUpdateRequestDto, product);
                     Products saved = productRepository.save(product);

                     AppLogManager.info(ProductServiceImpl.class,
                                   String.format("Product updated successfully with id: %d", saved.getId()));
              } catch (BusinessException | ResourceNotFoundException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   String.format("Error updating product with id: %d", productId), e);
                     throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
              }
       }

       @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
       public ProductResponseDto getProductById(Long productId) {
              try {
                     // Validate product ID
                     if (productId == null || productId <= 0) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format("Invalid product ID: %s", productId));
                            throw new BusinessException(StatusCode.INVALID_PRODUCT_ID);
                     }

                     Products product = productRepository.findById(productId)
                                   .orElseThrow(() -> {
                                          AppLogManager.warn(ProductServiceImpl.class,
                                                        String.format(PRODUCT_NOT_FOUND, productId));
                                          return new ResourceNotFoundException(
                                                        String.format(PRODUCT_NOT_FOUND, productId));
                                   });

                     return productMapper.toResponse(product);
              } catch (BusinessException | ResourceNotFoundException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   String.format("Error retrieving product with id: %d", productId), e);
                     throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
              }
       }

       @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
       public Page<ProductResponseDto> getAllProducts(String categorySlug, FilterProductCateType filterProductCateType,
                     CriteriaFilter criteriaFilter) {
              try {
                     // Validate criteria filter
                     if (criteriaFilter == null) {
                            AppLogManager.warn(ProductServiceImpl.class, "Criteria filter is null");
                            throw new BusinessException(StatusCode.VALIDATION_FAILED);
                     }

                     List<ProductResponseDto> products = productRepository
                                   .findAllByCategorySlug(categorySlug,
                                                 filterProductCateType != null ? filterProductCateType.name() : null,
                                                 criteriaFilter.toPageable("createdAt", Sort.Direction.DESC))
                                   .stream()
                                   .map(productMapper::toResponse)
                                   .toList();

                     AppLogManager.debug(ProductServiceImpl.class,
                                   String.format("Retrieved %d products (categorySlug: %s)",
                                                 products.size(), categorySlug));
                     return new PageImpl<>(products, criteriaFilter.toPageable(), products.size());
              } catch (BusinessException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   String.format("Error retrieving products (categorySlug: %s)", categorySlug), e);
                     throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
              }
       }

       @Transactional(rollbackFor = Exception.class)
       public void deleteProduct(Long productId) {
              try {
                     // Validate product ID
                     if (productId == null || productId <= 0) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format("Invalid product ID: %s", productId));
                            throw new BusinessException(StatusCode.INVALID_PRODUCT_ID);
                     }
                     var product = productRepository.findById(productId).orElseThrow(
                                   () -> new BusinessException(StatusCode.PRODUCT_NOT_FOUND));
                     product.setIsActive(false);
                     productRepository.save(product);
                     AppLogManager.info(ProductServiceImpl.class,
                                   String.format("Product deleted successfully with id: %d", productId));
              } catch (BusinessException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   String.format("Error deleting product with id: %d", productId), e);
                     throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
              }
       }

       @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
       public ProductResponseDto getProductBySlug(String slug) {
              try {
                     // Validate slug
                     if (slug == null || slug.isEmpty()) {
                            AppLogManager.warn(ProductServiceImpl.class, "Slug is null or empty");
                            throw new BusinessException(StatusCode.SLUG_REQUIRED);
                     }
                     var product = productRepository.findBySlug(slug).orElseThrow(
                                   () -> new BusinessException(StatusCode.PRODUCT_NOT_FOUND));
                     return productMapper.toResponse(product);
              } catch (BusinessException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   String.format("Error retrieving product with slug: %s", slug), e);
                     throw new BusinessException(StatusCode.INTERNAL_SERVER_ERROR);
              }
       }
}

