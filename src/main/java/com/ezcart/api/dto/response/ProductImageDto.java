package com.ezcart.api.dto.response;

public record ProductImageDto(
    Long id,
    String imageUrl,
    String altText,
    Integer displayOrder,
    Boolean isPrimary
) {}
