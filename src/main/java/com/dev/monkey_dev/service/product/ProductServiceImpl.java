package com.dev.monkey_dev.service.product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.domain.entity.Category;
import com.dev.monkey_dev.domain.entity.ProductImage;
import com.dev.monkey_dev.domain.entity.ProductVariation;
import com.dev.monkey_dev.domain.entity.Products;
import com.dev.monkey_dev.domain.respository.CategoryRepository;
import com.dev.monkey_dev.domain.respository.ProductRepository;
import com.dev.monkey_dev.domain.respository.ProductVariationRepository;
import com.dev.monkey_dev.domain.respository.ProductImageRepository;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.dto.mapper.ProductMapper;
import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.ProductCreateRequestDto;
import com.dev.monkey_dev.dto.request.ProductImageCreateDto;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.ProductUpdateRequestDto;
import com.dev.monkey_dev.dto.request.ProductVariationCreateDto;
import com.dev.monkey_dev.enums.FilterProductCateType;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.exception.ResourceNotFoundException;
import com.dev.monkey_dev.logging.AppLogManager;

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

       @Transactional
       public void createProduct(ProductCreateRequestDto productCreateRequestDto) {
              try {
                     // Validate request
                     if (productCreateRequestDto == null) {
                            AppLogManager.warn(ProductServiceImpl.class, "Product creation request is null");
                            throw new BusinessException(StatusCode.BAD_REQUEST,
                                          "Product creation request cannot be null");
                     }

                     // Validate user exists
                     if (!userRepository.existsById(productCreateRequestDto.userId())) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format(USER_NOT_FOUND, productCreateRequestDto.userId()));
                            throw new BusinessException(StatusCode.USER_NOT_FOUND,
                                          String.format(USER_NOT_FOUND, productCreateRequestDto.userId()));
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
                     // throw new BusinessException(StatusCode.BAD_REQUEST, CATEGORY_NOT_FOUND);
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
                                   productVariation.setPriceAdjustment(variation.priceAdjustment() == null ? 0.0
                                                 : variation.priceAdjustment().doubleValue());
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
                     throw new BusinessException(StatusCode.BAD_REQUEST,
                                   "Failed to create product: " + e.getMessage());
              }
       }

       @Transactional
       public void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {
              try {
                     // Validate product ID
                     if (productId == null || productId <= 0) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format("Invalid product ID: %s", productId));
                            throw new BusinessException(StatusCode.BAD_REQUEST, "Invalid product ID");
                     }

                     // Validate request
                     if (productUpdateRequestDto == null) {
                            AppLogManager.warn(ProductServiceImpl.class, "Product update request is null");
                            throw new BusinessException(StatusCode.BAD_REQUEST,
                                          "Product update request cannot be null");
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
                                   throw new BusinessException(StatusCode.BAD_REQUEST, CATEGORY_NOT_FOUND);
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
                     throw new BusinessException(StatusCode.BAD_REQUEST,
                                   "Failed to update product: " + e.getMessage());
              }
       }

       @Transactional(readOnly = true)
       public ProductResponseDto getProductById(Long productId) {
              try {
                     // Validate product ID
                     if (productId == null || productId <= 0) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format("Invalid product ID: %s", productId));
                            throw new BusinessException(StatusCode.BAD_REQUEST, "Invalid product ID");
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
                     throw new BusinessException(StatusCode.BAD_REQUEST,
                                   "Failed to retrieve product: " + e.getMessage());
              }
       }

       @Transactional(readOnly = true)
       public Page<ProductResponseDto> getAllProducts(String categorySlug, FilterProductCateType filterProductCateType,
                     CriteriaFilter criteriaFilter) {
              try {
                     // Validate criteria filter
                     if (criteriaFilter == null) {
                            AppLogManager.warn(ProductServiceImpl.class, "Criteria filter is null");
                            throw new BusinessException(StatusCode.BAD_REQUEST, "Criteria filter cannot be null");
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
                     throw new BusinessException(StatusCode.BAD_REQUEST,
                                   "Failed to retrieve products: " + e.getMessage());
              }
       }

       @Transactional
       public void deleteProduct(Long productId) {
              try {
                     // Validate product ID
                     if (productId == null || productId <= 0) {
                            AppLogManager.warn(ProductServiceImpl.class,
                                          String.format("Invalid product ID: %s", productId));
                            throw new BusinessException(StatusCode.BAD_REQUEST, "Invalid product ID");
                     }
                     var product = productRepository.findById(productId).orElseThrow(
                                   () -> new BusinessException(StatusCode.BAD_REQUEST, "Product not found"));
                     product.setIsActive(false);
                     productRepository.save(product);
                     AppLogManager.info(ProductServiceImpl.class,
                                   String.format("Product deleted successfully with id: %d", productId));
              } catch (BusinessException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   String.format("Error deleting product with id: %d", productId), e);
                     throw new BusinessException(StatusCode.BAD_REQUEST,
                                   "Failed to delete product: " + e.getMessage());
              }
       }

       @Transactional(readOnly = true)
       public ProductResponseDto getProductBySlug(String slug) {
              try {
                     // Validate slug
                     if (slug == null || slug.isEmpty()) {
                            AppLogManager.warn(ProductServiceImpl.class, "Slug is null or empty");
                            throw new BusinessException(StatusCode.BAD_REQUEST, "Slug is null or empty");
                     }
                     var product = productRepository.findBySlug(slug).orElseThrow(
                                   () -> new BusinessException(StatusCode.BAD_REQUEST, "Product not found"));
                     return productMapper.toResponse(product);
              } catch (BusinessException e) {
                     throw e;
              } catch (Exception e) {
                     AppLogManager.error(ProductServiceImpl.class,
                                   String.format("Error retrieving product with slug: %s", slug), e);
                     throw new BusinessException(StatusCode.BAD_REQUEST,
                                   "Failed to retrieve product: " + e.getMessage());
              }
       }
}
