package com.dev.monkey_dev.dto.request;

import java.math.BigDecimal;

public record ProductVariationDto(
    Long id,
    String name,
    String value,
    BigDecimal priceAdjustment,
    String sku,
    Integer stockQuantity
) {}
