package com.rental.api.order.response;

import com.rental.saas.common.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "订单响应")
public class OrderResponse {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单状态")
    private OrderStatus orderStatus;

    @Schema(description = "下单时间")
    private LocalDateTime createTime;

    @Schema(description = "驾驶人姓名")
    private String driverName;

    @Schema(description = "租车开始时间")
    private LocalDateTime startTime;

    @Schema(description = "租车结束时间")
    private LocalDateTime endTime;

    @Schema(description = "车型商品ID")
    private Long productId;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "车损押金")
    private BigDecimal damageDeposit;

    @Schema(description = "违章押金")
    private BigDecimal violationDeposit;

    @Schema(description = "是否支付押金")
    private Boolean isDepositPaid;
}