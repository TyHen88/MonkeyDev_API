package com.dev.monkey_dev.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductImageCreateDto(
    @NotBlank String imageUrl,
    String altText,
    Integer displayOrder,
    Boolean isPrimary
) {}