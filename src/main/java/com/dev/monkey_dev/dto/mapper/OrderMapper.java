package com.dev.monkey_dev.dto.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.*;

import com.dev.monkey_dev.domain.entity.Address;
import com.dev.monkey_dev.domain.entity.Order;
import com.dev.monkey_dev.domain.entity.OrderItem;
import com.dev.monkey_dev.domain.entity.Payment;
import com.dev.monkey_dev.dto.request.AddressDto;
import com.dev.monkey_dev.dto.request.OrderItemResponseDto;
import com.dev.monkey_dev.dto.request.OrderResponseDto;
import com.dev.monkey_dev.dto.request.PaymentSummaryDto;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "billingAddress", source = "billingAddress")
    @Mapping(target = "payment", source = "payment")
    @Mapping(target = "items", ignore = true)
    OrderResponseDto toResponse(Order order);

    AddressDto toAddressDto(Address address);
    PaymentSummaryDto toPaymentSummary(Payment payment);

    // Items
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productVariationId", source = "productVariation.id")
    OrderItemResponseDto toItemDto(OrderItem item);

    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}
