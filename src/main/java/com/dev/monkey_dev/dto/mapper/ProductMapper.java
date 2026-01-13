package com.dev.monkey_dev.dto.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.dev.monkey_dev.domain.entity.Category;
import com.dev.monkey_dev.domain.entity.ProductImage;
import com.dev.monkey_dev.domain.entity.ProductVariation;
import com.dev.monkey_dev.domain.entity.Products;
import com.dev.monkey_dev.dto.request.CategorySummaryDto;
import com.dev.monkey_dev.dto.request.ProductCreateRequestDto;
import com.dev.monkey_dev.dto.request.ProductImageDto;
import com.dev.monkey_dev.dto.request.ProductResponseDto;
import com.dev.monkey_dev.dto.request.ProductUpdateRequestDto;
import com.dev.monkey_dev.dto.request.ProductVariationDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ---------- Response mapping ----------
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categories", expression = "java(toCategorySummarySet(product.getCategories()))")
    @Mapping(target = "images", expression = "java(toImageDtoList(product.getImages()))")
    @Mapping(target = "variations", expression = "java(toVariationDtoList(product.getVariations()))")
    ProductResponseDto toResponse(Products product);

    CategorySummaryDto toCategorySummary(Category category);

    ProductImageDto toImageDto(ProductImage image);

    ProductVariationDto toVariationDto(ProductVariation variation);

    default Set<CategorySummaryDto> toCategorySummarySet(Set<Category> categories) {
        if (categories == null)
            return Set.of();
        return categories.stream().map(this::toCategorySummary).collect(Collectors.toSet());
    }

    default List<ProductImageDto> toImageDtoList(List<ProductImage> images) {
        if (images == null)
            return List.of();
        return images.stream().map(this::toImageDto).collect(Collectors.toList());
    }

    default List<ProductVariationDto> toVariationDtoList(List<ProductVariation> variations) {
        if (variations == null)
            return List.of();
        return variations.stream().map(this::toVariationDto).collect(Collectors.toList());
    }

    // ---------- Create mapping (Request -> Entity) ----------
    // Note: We do NOT map user/categories here because they need repositories.
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "weight", source = "weight")
    @Mapping(target = "length", source = "length")
    @Mapping(target = "width", source = "width")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "taxRate", source = "taxRate")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "isFeatured", source = "isFeatured")
    @Mapping(target = "isNew", source = "isNew")
    Products toEntity(ProductCreateRequestDto dto);

    // ---------- Update mapping ----------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProductUpdateRequestDto dto, @MappingTarget Products product);

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
