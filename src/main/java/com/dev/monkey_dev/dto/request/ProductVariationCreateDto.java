package com.dev.monkey_dev.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductVariationCreateDto(
    @NotBlank String name,
    @NotBlank String value,
    @NotNull BigDecimal priceAdjustment,
    String sku,
    @NotNull @Min(0) Integer stockQuantity
) {}
