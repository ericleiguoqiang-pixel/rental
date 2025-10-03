package com.rental.saas.order.dto;

import com.rental.saas.common.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "订单DTO")
public class OrderDTO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单状态")
    private OrderStatus orderStatus;

    @Schema(description = "下单时间")
    private LocalDateTime createTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "驾驶人姓名")
    private String driverName;

    @Schema(description = "驾驶人手机号")
    private String driverPhone;

    @Schema(description = "租车开始时间")
    private LocalDateTime startTime;

    @Schema(description = "租车结束时间")
    private LocalDateTime endTime;

    @Schema(description = "实际取车时间")
    private LocalDateTime actualPickupTime;

    @Schema(description = "实际还车时间")
    private LocalDateTime actualReturnTime;

    @Schema(description = "车牌号")
    private String licensePlate;

    @Schema(description = "取车方式:1-门店自取,2-送车上门")
    private Integer pickupType;

    @Schema(description = "还车方式:1-门店归还,2-上门取车")
    private Integer returnType;

    @Schema(description = "取车司机")
    private String pickupDriver;

    @Schema(description = "还车司机")
    private String returnDriver;

    @Schema(description = "基础租车费(元)")
    private BigDecimal basicRentalFee;

    @Schema(description = "服务费(元)")
    private BigDecimal serviceFee;

    @Schema(description = "保障费(元)")
    private BigDecimal insuranceFee;

    @Schema(description = "总金额(元)")
    private BigDecimal totalAmount;
}