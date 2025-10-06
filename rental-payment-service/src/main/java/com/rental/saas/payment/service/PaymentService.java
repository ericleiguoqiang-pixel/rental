package com.rental.saas.payment.service;

import com.rental.saas.payment.dto.PaymentRequest;
import com.rental.saas.payment.dto.PaymentResponse;

/**
 * 支付服务接口
 * 
 * @author Rental SaaS Team
 */
public interface PaymentService {

    /**
     * 处理支付请求
     * 
     * @param request 支付请求参数
     * @return 支付响应结果
     */
    PaymentResponse processPayment(PaymentRequest request);

    /**
     * 模拟支付成功回调
     * 
     * @param paymentNo 支付单号
     * @return 是否成功
     */
    boolean handlePaymentCallback(String paymentNo);
}