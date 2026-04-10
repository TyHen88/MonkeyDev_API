package com.ezcart.api.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezcart.api.common.api.ApiResponse;
import com.ezcart.api.controller.base.BaseApiRestController;
import com.ezcart.api.payload.payway.PaywayPurchaseRequest;
import com.ezcart.api.payload.payway.PaywayVerifyRequest;
import com.ezcart.api.service.payment.PaywayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wb/v1/payments/payway")
@RequiredArgsConstructor
@Tag(name = "PayWay", description = "ABA PayWay payment integration")
public class PaywayController extends BaseApiRestController {

    private final PaywayService paywayService;

    @PostMapping("/purchase")
    @Operation(summary = "Create PayWay transaction", description = "Creates a PayWay transaction and returns the checkout HTML")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createTransaction(@RequestBody @Valid PaywayPurchaseRequest request) {
        String html = paywayService.createTransaction(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                .body(html);
    }

    @PostMapping("/return")
    @Operation(summary = "PayWay callback", description = "Receive PayWay return_url callback")
    public ResponseEntity<ApiResponse<Object>> handleCallback(@RequestBody Map<String, Object> payload) {
        paywayService.handleCallback(payload);
        return successMessage("OK");
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify PayWay transaction", description = "Checks the transaction status with PayWay")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> verifyTransaction(@RequestBody @Valid PaywayVerifyRequest request) {
        return success(paywayService.verifyTransaction(request.getTranId()));
    }
}

