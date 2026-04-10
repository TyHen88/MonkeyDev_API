package com.ezcart.api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.ezcart.api.enums.OrderStatus;

public record OrderResponseDto(
    Long id,
    Long userId,
    String orderNumber,
    OrderStatus status,

    BigDecimal subtotal,
    BigDecimal taxAmount,
    BigDecimal shippingCost,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    String currency,

    AddressDto shippingAddress,
    AddressDto billingAddress,

    PaymentSummaryDto payment,

    List<OrderItemResponseDto> items,

    String notes,
    OffsetDateTime orderedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}

