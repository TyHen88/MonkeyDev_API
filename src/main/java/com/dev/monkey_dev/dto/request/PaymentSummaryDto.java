package com.dev.monkey_dev.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.dev.monkey_dev.enums.PaymentMethod;
import com.dev.monkey_dev.enums.PaymentStatus;

public record PaymentSummaryDto(
    Long id,
    PaymentMethod paymentMethod,
    PaymentStatus paymentStatus,
    String transactionId,
    BigDecimal amount,
    String currency,
    OffsetDateTime paymentDate
) {}
