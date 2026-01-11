package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

public record ProductUpdateRequestDto(
    @NotBlank String title,
    String description,

    @NotNull @PositiveOrZero BigDecimal price,
    String sku,

    @NotBlank @Size(min = 3, max = 3) String currency,

    BigDecimal weight,
    BigDecimal length,
    BigDecimal width,
    BigDecimal height,

    BigDecimal taxRate,

    @NotNull Boolean isActive,
    @NotNull Boolean isFeatured,
    @NotNull Boolean isNew,

    @NotEmpty Set<Long> categoryIds
) {}
