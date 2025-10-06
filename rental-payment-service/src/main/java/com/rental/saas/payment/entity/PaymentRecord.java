package com.rental.saas.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.rental.saas.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 支付记录实体
 * 
 * @author Rental SaaS Team
 */
@Data
@TableName("payment_record")
@Schema(description = "支付记录")
public class PaymentRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 支付单号
     */
    @TableField("payment_no")
    @Schema(description = "支付单号", example = "P202312010001")
    private String paymentNo;

    /**
     * 订单ID
     */
    @TableField("order_id")
    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    /**
     * 订单号
     */
    @TableField("order_no")
    @Schema(description = "订单号", example = "R202312010001")
    private String orderNo;

    /**
     * 支付类型:1-租车费,2-押金,3-违章费,4-其他费用
     */
    @TableField("payment_type")
    @Schema(description = "支付类型:1-租车费,2-押金,3-违章费,4-其他费用", example = "1")
    private Integer paymentType;

    /**
     * 支付金额(分)
     */
    @TableField("payment_amount")
    @Schema(description = "支付金额(分)", example = "10000")
    private Integer paymentAmount;

    /**
     * 支付方式:1-微信,2-支付宝,3-银行卡
     */
    @TableField("payment_method")
    @Schema(description = "支付方式:1-微信,2-支付宝,3-银行卡", example = "1")
    private Integer paymentMethod;

    /**
     * 支付状态:1-待支付,2-支付成功,3-支付失败,4-已退款
     */
    @TableField("payment_status")
    @Schema(description = "支付状态:1-待支付,2-支付成功,3-支付失败,4-已退款", example = "1")
    private Integer paymentStatus;

    /**
     * 第三方交易号
     */
    @TableField("third_party_trade_no")
    @Schema(description = "第三方交易号")
    private String thirdPartyTradeNo;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    /**
     * 退款金额(分)
     */
    @TableField("refund_amount")
    @Schema(description = "退款金额(分)", example = "0")
    private Integer refundAmount;

    /**
     * 退款时间
     */
    @TableField("refund_time")
    @Schema(description = "退款时间")
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    @TableField("refund_reason")
    @Schema(description = "退款原因")
    private String refundReason;
}