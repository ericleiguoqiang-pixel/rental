package com.rental.saas.order.dto;

import com.rental.saas.common.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单状态变更记录DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "订单状态变更记录DTO")
public class OrderStatusLogDTO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "原状态")
    private OrderStatus oldStatus;

    @Schema(description = "新状态")
    private OrderStatus newStatus;

    @Schema(description = "变更原因")
    private String changeReason;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "变更时间")
    private LocalDateTime changeTime;
}