package com.rental.saas.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "支付请求DTO")
public class PaymentRequest {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "支付类型:1-租车费,2-押金")
    private Integer paymentType;

    @Schema(description = "支付金额(元)")
    private BigDecimal amount;

    @Schema(description = "支付方式:1-微信,2-支付宝")
    private Integer paymentMethod;
}