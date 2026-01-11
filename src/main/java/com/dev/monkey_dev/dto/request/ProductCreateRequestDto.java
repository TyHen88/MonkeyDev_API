package com.dev.monkey_dev.dto.request;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductCreateRequestDto(

    @NotNull Long userId,

    @NotBlank String slug,
    @NotBlank String title,
    String description,

    @NotNull @PositiveOrZero BigDecimal price,
    String sku,

    @NotBlank @Size(min = 3, max = 3) String currency,

    @Positive BigDecimal weight,
    @Positive BigDecimal length,
    @Positive BigDecimal width,
    @Positive BigDecimal height,

    @PositiveOrZero BigDecimal taxRate,

    Boolean isActive,
    Boolean isFeatured,
    Boolean isNew,

    @NotEmpty Set<Long> categoryIds,

    List<ProductImageCreateDto> images,
    List<ProductVariationCreateDto> variations

) {}

