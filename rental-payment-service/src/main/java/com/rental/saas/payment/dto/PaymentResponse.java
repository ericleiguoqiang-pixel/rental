package com.rental.saas.payment.dto;

import com.rental.saas.common.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "支付响应DTO")
public class PaymentResponse {

    @Schema(description = "支付记录ID")
    private Long id;

    @Schema(description = "支付单号")
    private String paymentNo;

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

    @Schema(description = "支付状态")
    private PaymentStatus paymentStatus;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;
}