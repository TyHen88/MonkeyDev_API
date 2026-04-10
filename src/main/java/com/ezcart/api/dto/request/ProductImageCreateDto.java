package com.ezcart.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductImageCreateDto(
    @NotBlank String imageUrl,
    String altText,
    Integer displayOrder,
    Boolean isPrimary
) {}
