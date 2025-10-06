package com.rental.api.payment;

import com.rental.saas.common.response.ApiResponse;
import com.rental.api.payment.request.PaymentRequest;
import com.rental.api.payment.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 支付服务Feign客户端
 * 
 * @author Rental SaaS Team
 */
@FeignClient(name = "rental-payment-service", path = "/api")
public interface PaymentClient {

    /**
     * 发起支付
     * 
     * @param request 支付请求参数
     * @return 支付响应结果
     */
    @PostMapping("/payments")
    ApiResponse<PaymentResponse> initiatePayment(@RequestBody PaymentRequest request);

    /**
     * 支付回调
     * 
     * @param paymentNo 支付单号
     * @return 是否成功
     */
    @PostMapping("/payments/callback")
    ApiResponse<Boolean> paymentCallback(@RequestParam("paymentNo") String paymentNo);
}