package com.dev.monkey_dev.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "payway")
public class PaywayProperties {
    private String baseUrl = "https://checkout-sandbox.payway.com.kh";
    private String purchasePath = "/api/payment-gateway/v1/payments/purchase";
    private String checkTransactionPath = "/api/payment-gateway/v1/payments/check-transaction";

    private String merchantId;
    private String apiKey;

    private String returnUrl;
    private String cancelUrl;
    private String continueSuccessUrl;
    private String defaultCurrency = "USD";

    private boolean verifyOnCallback = true;

    private List<String> createHashFields = new ArrayList<>(
            List.of("req_time", "merchant_id", "tran_id", "amount", "currency"));
}
