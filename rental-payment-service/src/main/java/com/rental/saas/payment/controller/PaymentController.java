package com.rental.saas.payment.controller;

import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.payment.dto.PaymentRequest;
import com.rental.saas.payment.dto.PaymentResponse;
import com.rental.saas.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@Tag(name = "支付接口", description = "支付相关接口")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 发起支付
     */
    @PostMapping
    @Operation(summary = "发起支付", description = "发起支付请求")
    public ApiResponse<PaymentResponse> initiatePayment(
            @Parameter(description = "支付请求参数") @RequestBody PaymentRequest request) {
        
        log.info("发起支付: orderId={}, amount={}", request.getOrderId(), request.getAmount());
        
        try {
            PaymentResponse response = paymentService.processPayment(request);
            log.info("支付发起成功: paymentNo={}", response.getPaymentNo());
            //暂无实际支付，模拟支付成功回调
            paymentService.handlePaymentCallback(response.getPaymentNo());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("发起支付失败: error={}", e.getMessage(), e);
            return ApiResponse.error("发起支付失败: " + e.getMessage());
        }
    }

    /**
     * 模拟支付成功
     */
    @PostMapping("/simulate-success")
    @Operation(summary = "模拟支付成功", description = "模拟支付成功并回调")
    public ApiResponse<Boolean> simulatePaymentSuccess(
            @Parameter(description = "支付单号") @RequestParam String paymentNo) {
        
        log.info("模拟支付成功: paymentNo={}", paymentNo);
        
        try {
            boolean result = paymentService.handlePaymentCallback(paymentNo);
            if (result) {
                log.info("模拟支付成功处理完成: paymentNo={}", paymentNo);
                return ApiResponse.success(true);
            } else {
                log.warn("模拟支付成功处理失败: paymentNo={}", paymentNo);
                return ApiResponse.error("模拟支付成功处理失败");
            }
        } catch (Exception e) {
            log.error("模拟支付成功异常: paymentNo={}, error={}", paymentNo, e.getMessage(), e);
            return ApiResponse.error("模拟支付成功异常: " + e.getMessage());
        }
    }

    /**
     * 支付回调
     */
    @PostMapping("/callback")
    @Operation(summary = "支付回调", description = "处理支付回调通知")
    public ApiResponse<Boolean> paymentCallback(
            @Parameter(description = "支付单号") @RequestParam String paymentNo) {
        
        log.info("处理支付回调: paymentNo={}", paymentNo);
        
        try {
            boolean result = paymentService.handlePaymentCallback(paymentNo);
            if (result) {
                log.info("支付回调处理成功: paymentNo={}", paymentNo);
                return ApiResponse.success(true);
            } else {
                log.warn("支付回调处理失败: paymentNo={}", paymentNo);
                return ApiResponse.error("支付回调处理失败");
            }
        } catch (Exception e) {
            log.error("处理支付回调异常: paymentNo={}, error={}", paymentNo, e.getMessage(), e);
            return ApiResponse.error("处理支付回调异常: " + e.getMessage());
        }
    }
}