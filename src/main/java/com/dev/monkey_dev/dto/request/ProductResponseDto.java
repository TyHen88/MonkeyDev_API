package com.dev.monkey_dev.dto.request;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public record ProductResponseDto(
    Long id,
    Long userId,

    String slug,
    String title,
    String description,

    BigDecimal price,
    String sku,
    String currency,

    BigDecimal weight,
    BigDecimal length,
    BigDecimal width,
    BigDecimal height,

    BigDecimal taxRate,

    Boolean isActive,
    Boolean isFeatured,
    Boolean isNew,

    Set<CategorySummaryDto> categories,
    List<ProductImageDto> images,
    List<ProductVariationDto> variations,

    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
