package com.dev.monkey_dev.service.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.dev.monkey_dev.common.serialization.JsonUtils;
import com.dev.monkey_dev.payload.payway.PaywayItem;
import com.dev.monkey_dev.payload.payway.PaywayPurchaseRequest;
import com.dev.monkey_dev.properties.PaywayProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaywayService {
    private static final DateTimeFormatter REQ_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RestTemplate restTemplate;
    private final PaywayProperties props;

    public String createTransaction(PaywayPurchaseRequest request) {
        String reqTime = firstNonBlank(request.getReqTime(), nowUtc());
        String merchantId = firstNonBlank(props.getMerchantId(), request.getMerchantId());
        String currency = firstNonBlank(request.getCurrency(), props.getDefaultCurrency());

        requireNonBlank(merchantId, "merchant_id");
        requireNonBlank(request.getTranId(), "tran_id");
        requireNonBlank(request.getType(), "type");
        requireNonBlank(request.getPaymentOption(), "payment_option");
        requireNonBlank(currency, "currency");

        String returnUrl = firstNonBlank(request.getReturnUrl(), props.getReturnUrl());
        requireNonBlank(returnUrl, "return_url");

        String amount = formatAmount(request.getAmount());
        String items = buildItems(request);
        String hash = firstNonBlank(request.getHash(), generateCreateHash(
                merchantId, reqTime, request, amount, currency, items, returnUrl));

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("req_time", reqTime);
        form.add("merchant_id", merchantId);
        form.add("tran_id", request.getTranId());
        addIfNotBlank(form, "firstname", request.getFirstname());
        addIfNotBlank(form, "lastname", request.getLastname());
        addIfNotBlank(form, "email", request.getEmail());
        addIfNotBlank(form, "phone", request.getPhone());
        form.add("type", request.getType());
        form.add("payment_option", request.getPaymentOption());
        if (items != null) {
            form.add("items", items);
        }
        if (request.getShipping() != null) {
            form.add("shipping", formatAmount(request.getShipping()));
        }
        form.add("amount", amount);
        form.add("currency", currency);
        form.add("return_url", returnUrl);
        addIfNotBlank(form, "cancel_url", firstNonBlank(request.getCancelUrl(), props.getCancelUrl()));
        addIfNotBlank(form, "continue_success_url",
                firstNonBlank(request.getContinueSuccessUrl(), props.getContinueSuccessUrl()));
        addIfNotBlank(form, "custom_fields", request.getCustomFields());
        addIfNotBlank(form, "return_params", request.getReturnParams());
        form.add("hash", hash);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                props.getBaseUrl() + props.getPurchasePath(),
                entity,
                String.class);

        if (response.getBody() == null) {
            throw new IllegalStateException("PayWay returned empty response");
        }
        return response.getBody();
    }

    public Map<String, Object> verifyTransaction(String tranId) {
        requireNonBlank(tranId, "tran_id");
        String merchantId = requireNonBlank(props.getMerchantId(), "merchant_id");
        String apiKey = requireNonBlank(props.getApiKey(), "api_key");

        String reqTime = nowUtc();
        String hash = hmacSha512Base64(merchantId + tranId, apiKey);

        Map<String, String> payload = new HashMap<>();
        payload.put("req_time", reqTime);
        payload.put("merchant_id", merchantId);
        payload.put("tran_id", tranId);
        payload.put("hash", hash);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                props.getBaseUrl() + props.getCheckTransactionPath(),
                new HttpEntity<>(payload, headers),
                Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("PayWay returned empty response");
        }
        return body;
    }

    public void handleCallback(Map<String, Object> payload) {
        if (!props.isVerifyOnCallback()) {
            return;
        }
        Object tranId = payload.get("tran_id");
        if (tranId != null) {
            verifyTransaction(String.valueOf(tranId));
        }
    }

    private String generateCreateHash(String merchantId,
            String reqTime,
            PaywayPurchaseRequest request,
            String amount,
            String currency,
            String items,
            String returnUrl) {
        if (request.getHash() != null && !request.getHash().isBlank()) {
            return request.getHash();
        }
        String apiKey = requireNonBlank(props.getApiKey(), "api_key");
        List<String> fields = props.getCreateHashFields();
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("payway.create-hash-fields is not configured");
        }

        Map<String, String> values = new HashMap<>();
        values.put("req_time", reqTime);
        values.put("merchant_id", merchantId);
        values.put("tran_id", request.getTranId());
        values.put("firstname", request.getFirstname());
        values.put("lastname", request.getLastname());
        values.put("email", request.getEmail());
        values.put("phone", request.getPhone());
        values.put("type", request.getType());
        values.put("payment_option", request.getPaymentOption());
        values.put("items", items);
        values.put("shipping", request.getShipping() == null ? null : formatAmount(request.getShipping()));
        values.put("amount", amount);
        values.put("currency", currency);
        values.put("return_url", returnUrl);
        values.put("cancel_url", firstNonBlank(request.getCancelUrl(), props.getCancelUrl()));
        values.put("continue_success_url",
                firstNonBlank(request.getContinueSuccessUrl(), props.getContinueSuccessUrl()));
        values.put("custom_fields", request.getCustomFields());
        values.put("return_params", request.getReturnParams());

        StringBuilder payload = new StringBuilder();
        for (String field : fields) {
            payload.append(Objects.toString(values.get(field), ""));
        }
        return hmacSha512Base64(payload.toString(), apiKey);
    }

    private static String buildItems(PaywayPurchaseRequest request) {
        if (request.getItems() != null && !request.getItems().isBlank()) {
            return request.getItems();
        }
        List<PaywayItem> items = request.getItemsList();
        if (items == null || items.isEmpty()) {
            return null;
        }
        String json = JsonUtils.writeValueAsSingleLineString(items);
        if (json == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private static String formatAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String nowUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(REQ_TIME_FORMAT);
    }

    private static void addIfNotBlank(MultiValueMap<String, Object> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.add(key, value);
        }
    }

    private static String firstNonBlank(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    private static String hmacSha512Base64(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate HMAC hash", ex);
        }
    }
}
